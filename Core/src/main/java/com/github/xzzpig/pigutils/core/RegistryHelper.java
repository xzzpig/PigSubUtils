package com.github.xzzpig.pigutils.core;

import com.github.xzzpig.pigutils.annoiation.API;
import com.github.xzzpig.pigutils.annoiation.NotNull;
import com.github.xzzpig.pigutils.annoiation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Windows注册表的帮助类
 * 使用仅限于Windows
 */
public class RegistryHelper {
    /**
     * 通过exec读取注册表获取Path
     * Path位置是CurrentUser的
     */
    @API
    public static String getUserPath() throws IOException {
        return getValue("HKEY_CURRENT_USER\\Environment", "Path");
    }

    /**
     * 从注册表获取值
     */
    @API
    public static String getValue(String dir, String key) throws IOException {
        String command = "reg query \"" + dir + "\" /v " + key;
        Process ps = Runtime.getRuntime().exec(command);
        ps.getOutputStream().close();
        InputStreamReader i = new InputStreamReader(ps.getInputStream());
        String line;
        BufferedReader ir = new BufferedReader(i);
        StringBuilder sb = new StringBuilder();
        while ((line = ir.readLine()) != null) {
            sb.append(line);
        }
        String[] paths = sb.toString().split("    ");
        return paths[paths.length - 1];
    }

    /**
     * 通过exec读取注册表获取Path
     * Path位置是System的
     */
    @API
    public static String getSystemPath() throws IOException {
        return getValue("HKEY_LOCAL_MACHINE\\SYSTEM\\ControlSet001\\Control\\Session Manager\\Environment", "Path");
    }

    /**
     * 设置注册表的值
     *
     * @param type is REG_SZ if null
     */
    @API
    public static void setValue(@NotNull String dir, @NotNull String key, @Nullable String type, @NotNull String value) throws IOException {
        deleteValue(dir, key);
        if (type == null)
            type = "REG_SZ";
        String command = "reg add \"" + dir + "\" /v " + key + " /t " + type + " /d " + value;
        Process ps = Runtime.getRuntime().exec(command);
        ps.getOutputStream().close();
    }

    @API
    public static void deleteValue(@NotNull String dir, @NotNull String key) throws IOException {
        String command = "reg delete \"" + dir + "\" /f /v " + key;
        Process ps = Runtime.getRuntime().exec(command);
        ps.getOutputStream().close();
    }

    @API
    public static void addUserPath(@NotNull String path) throws IOException {
        String upath = getUserPath();
        path = upath + ";" + path + ";";
        while(path.contains(";;")){
            path = path.replace(";;", ";");
        }
        setValue("HKEY_CURRENT_USER\\Environment", "Path", null, path);
    }
}
