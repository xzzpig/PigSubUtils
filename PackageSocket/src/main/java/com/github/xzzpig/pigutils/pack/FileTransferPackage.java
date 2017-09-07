package com.github.xzzpig.pigutils.pack;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.xzzpig.pigutils.annoiation.NotNull;
import com.github.xzzpig.pigutils.annoiation.Nullable;
import com.github.xzzpig.pigutils.core.TransformManager;
import com.github.xzzpig.pigutils.file.ExtendFile;
import com.github.xzzpig.pigutils.json.JSONException;
import com.github.xzzpig.pigutils.json.JSONObject;
import com.github.xzzpig.pigutils.pack.socket.PackageSocket;
import com.github.xzzpig.pigutils.pack.socket.eventdrive.EDPackageSocketClient;
import com.github.xzzpig.pigutils.pack.socket.eventdrive.EDPackageSocketServer;
import com.github.xzzpig.pigutils.pack.socket.eventdrive.PackageSocketPackageEvent;
import com.github.xzzpig.pigutils.reflect.MethodUtils;
import com.github.xzzpig.pigutils.thread.TimeThread;

public class FileTransferPackage extends WrapperPackage {

    /**
     * 每个包的大小(仅限发送方有效)
     */
    public static int FileDetailPackageLength = 1024 * 16;
    /**
     * 最大上传速度(仅限发送方有效)<br/>
     * -1为无限制<br/>
     * 默认:-1<br/>
     * 单位:Bps
     */
    public static int MaxSpeed = -1;

    /**
     * 接收超时
     * 单位:ms
     */
    public static long Timeout = 5000;

    public static Map<Long, FileOutputStream> filemap_Receiver = new Hashtable<>();
    public static Map<Long, TimeThread> timethread_Receiver = new Hashtable<>();
    public static Map<Long, AtomicBoolean> timeout_Sender = new Hashtable<>();
    public static Map<Long, File> filemap_Sender = new Hashtable<>();

    public FileTransferPackage(Package pack) {
        super(pack);
    }

    public FileTransferPackage(@Nullable String type, @Nullable byte[] data) {
        super(type, data);
    }

    public static void addSupport(EDPackageSocketClient client) {
        client.regRunner((PackageSocketPackageEvent event) -> onPackage(client, event.getPackage()));
    }

    public static void addSupport(EDPackageSocketServer server) {
        server.regRunner((PackageSocketPackageEvent event) -> onPackage(event.getPackageSocket(), event.getPackage()));
    }

    // Receiver
    private static void onFileTransferContentPackage(PackageSocket socket, Package pack) {
        FileTransferPackage pack2 = new FileTransferPackage(pack.getType(), pack.getData());
        long fid = pack2.getFid();
        timethread_Receiver.get(fid).resetTime(System.currentTimeMillis() + Timeout);
        FileOutputStream fout = filemap_Receiver.get(fid);
        synchronized (fout) {
            try {
                fout.write(pack2.getRemainedData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Receiver
    private static void onFileTransferFinishPackage(PackageSocket socket, Package pack) {
        FileTransferPackage pack2 = new FileTransferPackage(pack.getType(), pack.getData());
        long fid = pack2.getFid();
        try {
            filemap_Receiver.get(fid).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        filemap_Receiver.remove(fid);
        timethread_Receiver.get(fid).interrupt();
        timethread_Receiver.remove(fid);
    }

    // Sender
    private static void onFileTransferPerparedPackage(PackageSocket socket, Package pack) {
        FileTransferPackage pack2 = new FileTransferPackage(pack.getType(), pack.getData());
        long fid = pack2.getFid();
        byte b = pack2.getRemainedData()[0];
        if (b == 0) {
            filemap_Sender.remove(fid);
            return;
        }
        timeout_Sender.put(fid, new AtomicBoolean(false));
        startSendFile(socket, filemap_Sender.get(fid), fid);
    }

    // Sender
    private static void onFileTransferTimeoutPackage(PackageSocket socket, Package pack2) {
        long fid = Long.parseLong(pack2.getStringData());
        if (timeout_Sender.containsKey(fid)) {
            timeout_Sender.get(fid).set(true);
        }
    }

    // Receiver
    private static void onFileTransferStartPackage(PackageSocket socket, Package pack) {
        JSONObject json = null;
        json = new JSONObject(pack.getStringData());
        File file = TransformManager.transform(json, File.class);
        byte b = 0;
        if (file != null) {
            boolean cont = true;
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                try {
                    if (!file.createNewFile())
                        throw new IOException("create " + file.getName() + " failed");
                } catch (IOException e) {
                    e.printStackTrace();
                    cont = false;
                }
            }
            if (cont) {
                FileOutputStream fout = null;
                try {
                    fout = new FileOutputStream(file, false);
                    filemap_Receiver.put(json.getLong("fid"), fout);
                    b = 1;
                    long fid = json.getLong("fid");
                    timethread_Receiver.put(fid, new TimeThread(System.currentTimeMillis() + Timeout) {
                        @Override
                        public void realRun() {
                            socket.send(new Package("FileTransferTimeoutPackage", fid + ""));
                        }
                    });
                    timethread_Receiver.get(fid).start();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    cont = false;
                }
            }
        }
        Package pack1 = new Package("FileTransferPerparedPackage", new byte[]{b});
        FileTransferPackage pack2 = new FileTransferPackage(pack1);
        pack2.setFid(json.getLong("fid"));
        socket.send(pack2);
    }

    public static void onPackage(PackageSocket socket, Package pack) {
        switch (pack.getType()) {
            case "FileTransferStartPackage":
                onFileTransferStartPackage(socket, pack);
                break;
            case "FileTransferPerparedPackage":
                onFileTransferPerparedPackage(socket, pack);
                break;
            case "FileTransferContentPackage":
                onFileTransferContentPackage(socket, pack);
                break;
            case "FileTransferFinishPackage":
                onFileTransferFinishPackage(socket, pack);
                break;
            case "FileTransferTimeoutPackage":
                onFileTransferTimeoutPackage(socket, pack);
                break;
            default:
                break;
        }
    }

    // Sender
    public static void sendFile(PackageSocket socket, @NotNull File file, @NotNull File baseDir, boolean synch) {
        MethodUtils.checkThisArgs(socket, file, baseDir, synch);
        if (!baseDir.isDirectory())
            throw new IllegalArgumentException("baseDir should be Directory");
        if (!file.exists())
            throw new IllegalArgumentException("file should exists");
        JSONObject fileInfo = new JSONObject();
        try {
            fileInfo.put("name", file.getCanonicalPath().replace(baseDir.getCanonicalPath(), "."));
        } catch (JSONException | IOException e1) {
            throw new RuntimeException(e1);
        }
        long fid = System.currentTimeMillis();
        filemap_Sender.put(fid, file);
        fileInfo.put("fid", fid);
        fileInfo.put("size", file.length());
        Package pack = new Package("FileTransferStartPackage", fileInfo.toString());
        socket.send(pack);
        if (synch) {
            while (filemap_Sender.containsKey(fid))
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
        }
        // System.out.println(fileInfo);
    }

    private static void startSendFile(PackageSocket socket, File file, long fid) {
        new Thread(() -> {
            Package pack = new Package();
            pack.type = "FileTransferContentPackage";
            FileTransferPackage transferPackage = new FileTransferPackage(pack);
            transferPackage.setFid(fid);
            ExtendFile extendFile = new ExtendFile(file);
            byte[] bs = new byte[FileDetailPackageLength];
            AtomicBoolean timeout = timeout_Sender.get(fid);
            extendFile.withInputStream(in -> {
                try {
                    int len = 0;
                    while ((len = in.read(bs)) != -1) {
                        if (len == bs.length) {
                            pack.data = bs;
                        } else {
                            pack.data = Arrays.copyOf(bs, len);
                        }
                        if (timeout.get())
                            break;
                        socket.send(transferPackage, MaxSpeed);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            });
            pack.type = "FileTransferFinishPackage";
            byte success = timeout.get() ? (byte) 0 : (byte) 1;
            pack.data = new byte[]{success};
            socket.send(transferPackage);
            filemap_Sender.remove(fid);
            timeout_Sender.remove(fid);
        }).start();
    }

    @Override
    public byte[] getData() {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try {
            bout.write(data);
            bout.write(getRemainedData());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            return bout.toByteArray();
        } finally {
            try {
                bout.close();
            } catch (IOException e) {
            }
        }
    }

    public long getFid() {
        ByteArrayInputStream bin = new ByteArrayInputStream(data);
        try {
            return getFid(bin);
        } finally {
            try {
                bin.close();
            } catch (IOException e) {
            }
        }
    }

    public void setFid(long fid) {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        String fidStr = fid + "";
        try {
            bout.write(int2Bytes(fidStr.length()));
            bout.write(fidStr.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        data = bout.toByteArray();
        try {
            bout.close();
        } catch (IOException e) {
        }
    }

    private long getFid(ByteArrayInputStream bin) {
        int i = 0;
        int fidLen = 0;
        do {
            i = bin.read();
            fidLen += i;
        } while (i == 255);
        byte[] bs = new byte[fidLen];
        try {
            bin.read(bs);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Long.parseLong(new String(bs));
    }

    public byte[] getRemainedData() {
        return getWrappedPackage().getData();
    }

    @Override
    protected void unWrapPackage(String type, ByteArrayInputStream data) {
        long fid = getFid(data);
        setFid(fid);
        super.unWrapPackage(type, data);
    }
}
