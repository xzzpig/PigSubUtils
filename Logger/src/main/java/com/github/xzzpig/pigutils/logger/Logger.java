package com.github.xzzpig.pigutils.logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.github.xzzpig.pigutils.json.JSONArray;
import com.github.xzzpig.pigutils.json.JSONException;
import com.github.xzzpig.pigutils.json.JSONObject;
import com.github.xzzpig.pigutils.json.JSONTokener;
import com.github.xzzpig.pigutils.reflect.ClassUtils;
import com.github.xzzpig.pigutils.reflect.MethodUtils;

public class Logger {

	private static final class LogInstance {
		private LogFormater logFormater;
		private LogLevel logLevel;
		private List<LogPrinter> logPrinters;
		private JSONObject config;
		private AnnotatedElement element;

		public LogInstance log(LogLevel level, Object... objs) {
			if (level.getLevel() < logLevel.getLevel())
				return this;
			String log = logFormater.format(element, level, config, objs);
			logPrinters.forEach(p -> p.print(log));
			return this;
		}

		private LogInstance init(LogConfig config, AnnotatedElement element) {
			if (!config.level().equalsIgnoreCase("extened")) {
				logLevel = LogLevel.getLevel(config.level());
			}
			if (!config.formater().equalsIgnoreCase("extened")) {
				logFormater = LogFormater.getFormater(config.formater());
			}
			if (logFormater.accept(element))
				this.element = element;
			if (config.printer().length != 0) {
				logPrinters = null;
				for (String sPrinter : config.printer()) {
					addLogPrinter(LogPrinter.getPrinter(sPrinter));
				}
			}
			return this;
		}

		private LogInstance init(JSONObject config) {
			this.config = config;
			if (!config.optString("level", "extened").equalsIgnoreCase("extened")) {
				logLevel = LogLevel.getLevel(config.optString("level", "INFO"));
			}
			if (!config.optString("formater", "extened").equalsIgnoreCase("extened")) {
				logFormater = LogFormater.getFormater(config.optString("formater", "String"));
			}
			if (logFormater.accept(config))
				this.config = config;
			if (config.has("printer")) {
				try {
					addLogPrinter(LogPrinter.getPrinter(config.getString("printer")));
				} catch (Exception e) {
					for (Object sPrinter : config.getJSONArray("printer").toList()) {
						addLogPrinter(LogPrinter.getPrinter(sPrinter + ""));
					}
				}
			}
			return this;
		}

		private void addLogPrinter(LogPrinter printer) {
			if (logPrinters == null)
				logPrinters = new ArrayList<>();
			if (logPrinters.contains(printer))
				return;
			logPrinters.add(printer);
		}

		@Override
		public LogInstance clone() {
			LogInstance instance = new LogInstance();
			instance.logFormater = logFormater;
			instance.logLevel = logLevel;
			instance.logPrinters = new ArrayList<>(logPrinters);
			instance.config = config;
			instance.element = element;
			return instance;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof LogInstance))
				return false;
			LogInstance instance = (LogInstance) obj;
			if (!instance.logFormater.equals(logFormater))
				return false;
			if (instance.logPrinters.size() != logPrinters.size())
				return false;
			if (!instance.logPrinters.containsAll(logPrinters))
				return false;
			return true;
		}
	}

	private static JSONObject projectLoggerConfig;
	private static Logger projectLogger;
	private static LogInstance defaultLogInstance;

	static {
		LogFormater.addFormater(new StringLogFormater());
		LogPrinter.addPrinter(new ConsoleLogPrinter());
		File config = new File("logconfig.json");
		if (config.exists() && config.isFile()) {
			try {
				projectLoggerConfig = new JSONObject(new JSONTokener(new FileInputStream(config)));
			} catch (JSONException | FileNotFoundException e) {
			}
		}
		defaultLogInstance = new LogInstance();
		defaultLogInstance.logFormater = LogFormater.getFormater("String");
		defaultLogInstance.logLevel = LogLevel.INFO;
		defaultLogInstance.addLogPrinter(LogPrinter.getPrinter("Console"));
		projectLogger = new Logger();
		projectLogger.init(projectLoggerConfig);
		if (projectLogger.logLogInstances == null)
			projectLogger.addLogInstance(defaultLogInstance.clone());
	}

	/**
	 * 获取Logger对象<br/>
	 * 按如下顺序添加 {@link LogInstance}对象:<br/>
	 * <ol>
	 * <li>项目根目录下的 logconfig.json-> {@link JSONObject}</li>
	 * <li>foreach( {@link Exception#getStackTrace()})
	 * <ol>
	 * <li>{@link StackTraceElement}->
	 * {@link Class#getResource(logconfig.json)}-> {@link JSONObject}</li>
	 * <li>{@link StackTraceElement}->class的 {@link LogConfig}注解</li>
	 * <li>{@link StackTraceElement}->method的 {@link LogConfig}注解</li>
	 * </ol>
	 * </li>
	 * <li>默认配置:
	 * <ul>
	 * <li>{@link LogConfig#level()}= {@link LogConfig#INFO}</li>
	 * <li>{@link LogConfig#formater()}= {@link StringLogFormater}</li>
	 * <li>{@link LogConfig#printer()}= {@link ConsoleLogPrinter}</li>
	 * </ul>
	 * </li>
	 * </ol>
	 */
	public static Logger getLogger() {
		Logger logger = new Logger();
		projectLogger.logLogInstances.forEach(logger::addLogInstance);
		// StackTraceElement[] trace = new Exception().getStackTrace();
		for (int i = new Exception().getStackTrace().length; i > 1; i--) {
			Class<?> clazz = ClassUtils.getStackClass(i);
			try {
				JSONObject json = new JSONObject(new JSONTokener(clazz.getResourceAsStream("logconfig.json")));
				logger.init(json);
			} catch (Exception e) {
			}
			if (clazz != null) {
				if (clazz.isAnnotationPresent(LogConfig.class)) {
					LogConfig logConfig = clazz.getAnnotation(LogConfig.class);
					logger.init(logConfig, clazz);
				}
			}
			Method m = MethodUtils.getStackMethod(i);
			if (m != null && m.isAnnotationPresent(LogConfig.class)) {
				LogConfig logConfig = m.getAnnotation(LogConfig.class);
				logger.init(logConfig, m);
			}
		}
		// Method m = MethodUtils.getStackMethod(i);
		// if (m != null && m.isAnnotationPresent(LogConfig.class)) {
		// LogConfig logConfig = m.getAnnotation(LogConfig.class);
		// logger.init(logConfig, m);
		// }
		// Class<?> clazz = ClassUtils.getStackClass(i);
		// if (clazz == null)
		// continue;
		// if (clazz.isAnnotationPresent(LogConfig.class)) {
		// LogConfig logConfig = clazz.getAnnotation(LogConfig.class);
		// logger.init(logConfig, clazz);
		// }
		// if (logger.isInited())
		// return logger;
		//
		// for (Field field : clazz.getDeclaredFields()) {
		// if (field.isAnnotationPresent(LogConfig.class)) {
		// boolean access = field.isAccessible();
		// if (Logger.class.isAssignableFrom(field.getType())) {
		// try {
		// field.setAccessible(true);
		// Logger logger2 = (Logger) field.get(null);
		// if (logger2 != null) {
		// if (logger.logFormater == null && logger2.logFormater != null)
		// logger.logFormater = logger2.logFormater;
		// if (logger.logLevel == null && logger2.logLevel != null)
		// logger.logLevel = logger2.logLevel;
		// if (logger.logPrinters == null && logger2.logPrinters != null)
		// logger.logPrinters = logger2.logPrinters;
		// }
		// } catch (Exception e) {
		// } finally {
		// field.setAccessible(access);
		// }
		// } else if (LogLevel.class.isAssignableFrom(field.getType())) {
		// try {
		// field.setAccessible(true);
		// LogLevel fobj = (LogLevel) field.get(null);
		// if (fobj != null) {
		// logger.logLevel = fobj;
		// }
		// } catch (Exception e) {
		// } finally {
		// field.setAccessible(access);
		// }
		// } else if (LogFormater.class.isAssignableFrom(field.getType())) {
		// try {
		// field.setAccessible(true);
		// LogFormater fobj = (LogFormater) field.get(null);
		// if (fobj != null) {
		// logger.logFormater = fobj;
		// }
		// } catch (Exception e) {
		// } finally {
		// field.setAccessible(access);
		// }
		// } else if (LogPrinter.class.isAssignableFrom(field.getType())) {
		// try {
		// field.setAccessible(true);
		// LogPrinter fobj = (LogPrinter) field.get(null);
		// if (fobj != null) {
		// logger.addLogPrinter(fobj);
		// }
		// } catch (Exception e) {
		// } finally {
		// field.setAccessible(access);
		// }
		// }
		// if (logger.isInited())
		// return logger;
		// }
		// }
		//
		// if (projectLoggerConfig != null) {
		// logger.init(projectLoggerConfig);
		// if (logger.isInited())
		// return logger;
		// }
		// if (logger.logLevel == null)
		// logger.logLevel = LogLevel.INFO;
		// if (logger.logFormater == null)
		// logger.logFormater = LogFormater.getFormater("String");
		// if (logger.logPrinters == null)
		// logger.addLogPrinter(LogPrinter.getPrinter("Console"));
		return logger;
	}

	private void init(LogConfig logConfig, AnnotatedElement m) {
		addLogInstance(logLogInstances.get(0).clone().init(logConfig, m));
	}

	private List<LogInstance> logLogInstances;

	private Logger() {
	}

	public Logger addLogInstance(LogInstance instance) {
		if (logLogInstances == null)
			logLogInstances = new ArrayList<>();
		if (instance == null)
			return this;
		Iterator<LogInstance> ir = logLogInstances.iterator();
		while (ir.hasNext()) {
			if (ir.next().equals(instance))
				ir.remove();
		}
		logLogInstances.add(0, instance);
		return this;
	}

	private Logger init(JSONObject json) {
		if (json == null)
			return this;
		JSONArray arr = json.optJSONArray("instance");
		if (logLogInstances == null || logLogInstances.size() == 0)
			addLogInstance(defaultLogInstance);
		if (arr == null)
			return this;
		for (int i = 0; i < arr.length(); i++) {
			addLogInstance(logLogInstances.get(0).clone().init(arr.optJSONObject(i)));
		}
		return this;
	}

	public Logger debug(Object... objs) {
		return log(LogLevel.DEBUG, objs);
	}

	public Logger error(Object... objs) {
		return log(LogLevel.ERROR, objs);
	}

	public Logger fatal(Object... objs) {
		return log(LogLevel.FATAL, objs);
	}

	public Logger info(Object... objs) {
		return log(LogLevel.INFO, objs);
	}

	// private boolean isInited() {
	// return logLevel == null ? false : logPrinters == null ? false :
	// logFormater == null ? false : true;
	// }

	public Logger log(LogLevel level, Object... objs) {
		logLogInstances.forEach(ins -> ins.log(level, objs));
		return this;
	}

	public Logger warn(Object... objs) {
		return log(LogLevel.WARN, objs);
	}
}
