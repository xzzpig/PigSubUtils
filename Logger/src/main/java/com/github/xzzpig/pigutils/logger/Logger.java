package com.github.xzzpig.pigutils.logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.github.xzzpig.pigutils.json.JSONException;
import com.github.xzzpig.pigutils.json.JSONObject;
import com.github.xzzpig.pigutils.json.JSONTokener;
import com.github.xzzpig.pigutils.reflect.ClassUtils;
import com.github.xzzpig.pigutils.reflect.MethodUtils;

public class Logger {

	private static JSONObject projectLoggerConfig;

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
	}

	/**
	 * 获取Logger对象<br/>
	 * 按如下顺序初始化Logger对象:<br/>
	 * <ol>
	 * <li>foreach( {@link Exception#getStackTrace()})
	 * <ol>
	 * <li>{@link StackTraceElement}->method的 {@link LogConfig}注解</li>
	 * <li>{@link StackTraceElement}->class的 {@link LogConfig}注解</li>
	 * <li>{@link StackTraceElement}->class中被 {@link LogConfig}注解的
	 * {@link Logger}</li>
	 * <li>{@link StackTraceElement}->
	 * {@link Class#getResource(logconfig.json)}-> {@link JSONObject}</li>
	 * </ol>
	 * </li>
	 * <li>项目根目录下的 logconfig.json-> {@link JSONObject}</li>
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
		for (int i = 1; i <= new Exception().getStackTrace().length; i++) {
			Method m = MethodUtils.getStackMethod(i);
			if (m != null && m.isAnnotationPresent(LogConfig.class)) {
				LogConfig logConfig = m.getAnnotation(LogConfig.class);
				logger.init(logConfig, m);
			}
			if (logger.isInited())
				return logger;
			Class<?> clazz = ClassUtils.getStackClass(i);
			if (clazz == null)
				continue;
			if (clazz.isAnnotationPresent(LogConfig.class)) {
				LogConfig logConfig = clazz.getAnnotation(LogConfig.class);
				logger.init(logConfig, clazz);
			}
			if (logger.isInited())
				return logger;

			for (Field field : clazz.getDeclaredFields()) {
				if (field.isAnnotationPresent(LogConfig.class)) {
					boolean access = field.isAccessible();
					if (Logger.class.isAssignableFrom(field.getType())) {
						try {
							field.setAccessible(true);
							Logger logger2 = (Logger) field.get(null);
							if (logger2 != null) {
								if (logger.logFormater == null && logger2.logFormater != null)
									logger.logFormater = logger2.logFormater;
								if (logger.logLevel == null && logger2.logLevel != null)
									logger.logLevel = logger2.logLevel;
								if (logger.logPrinters == null && logger2.logPrinters != null)
									logger.logPrinters = logger2.logPrinters;
							}
						} catch (Exception e) {
						} finally {
							field.setAccessible(access);
						}
					} else if (LogLevel.class.isAssignableFrom(field.getType())) {
						try {
							field.setAccessible(true);
							LogLevel fobj = (LogLevel) field.get(null);
							if (fobj != null) {
								logger.logLevel = fobj;
							}
						} catch (Exception e) {
						} finally {
							field.setAccessible(access);
						}
					} else if (LogFormater.class.isAssignableFrom(field.getType())) {
						try {
							field.setAccessible(true);
							LogFormater fobj = (LogFormater) field.get(null);
							if (fobj != null) {
								logger.logFormater = fobj;
							}
						} catch (Exception e) {
						} finally {
							field.setAccessible(access);
						}
					} else if (LogPrinter.class.isAssignableFrom(field.getType())) {
						try {
							field.setAccessible(true);
							LogPrinter fobj = (LogPrinter) field.get(null);
							if (fobj != null) {
								logger.addLogPrinter(fobj);
							}
						} catch (Exception e) {
						} finally {
							field.setAccessible(access);
						}
					}
					if (logger.isInited())
						return logger;
				}
			}

			try {
				JSONObject json = new JSONObject(new JSONTokener(clazz.getResourceAsStream("logconfig.json")));
				logger.init(json);
				if (logger.isInited())
					return logger;
			} catch (Exception e) {
			}
		}
		if (projectLoggerConfig != null) {
			logger.init(projectLoggerConfig);
			if (logger.isInited())
				return logger;
		}
		if (logger.logLevel == null)
			logger.logLevel = LogLevel.INFO;
		if (logger.logFormater == null)
			logger.logFormater = LogFormater.getFormater("String");
		if (logger.logPrinters == null)
			logger.addLogPrinter(LogPrinter.getPrinter("Console"));
		return logger;
	}

	private LogFormater logFormater;
	private LogLevel logLevel;

	private List<LogPrinter> logPrinters;

	private Logger() {
	}

	public Logger addLogPrinter(LogPrinter printer) {
		if (logPrinters == null)
			logPrinters = new ArrayList<>();
		logPrinters.add(printer);
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

	public LogFormater getLogFormater() {
		return logFormater;
	}

	public LogLevel getLogLevel() {
		return logLevel;
	}

	public Logger info(Object... objs) {
		return log(LogLevel.INFO, objs);
	}

	private void init(JSONObject config) {
		if (logLevel == null && !config.optString("level", "extened").equalsIgnoreCase("extened")) {
			logLevel = LogLevel.getLevel(config.optString("level", "extened"));
		}
		if (logFormater == null && !config.optString("formater", "extened").equalsIgnoreCase("extened")) {
			logFormater = LogFormater.getFormater(config.optString("formater", "extened"));
			if (logFormater != null)
				logFormater.config = config;
		}
		if (logPrinters == null && config.has("printer")) {
			try {
				addLogPrinter(LogPrinter.getPrinter(config.getString("printer")));
			} catch (Exception e) {
				for (Object sPrinter : config.getJSONArray("printer").toList()) {
					addLogPrinter(LogPrinter.getPrinter(sPrinter + ""));
				}
			}
		}
	}

	private void init(LogConfig config, AnnotatedElement element) {
		if (logLevel == null && !config.level().equalsIgnoreCase("extened")) {
			logLevel = LogLevel.getLevel(config.level());
		}
		if (logFormater == null && !config.formater().equalsIgnoreCase("extened")) {
			logFormater = LogFormater.getFormater(config.formater());
			if (logFormater != null)
				logFormater.element = element;
		}
		if (logPrinters == null && config.printer().length != 0) {
			for (String sPrinter : config.printer()) {
				addLogPrinter(LogPrinter.getPrinter(sPrinter));
			}
		}
	}

	private boolean isInited() {
		return logLevel == null ? false : logPrinters == null ? false : logFormater == null ? false : true;
	}

	public Logger log(LogLevel level, Object... objs) {
		if (level.getLevel() < logLevel.getLevel())
			return this;
		String log = logFormater.format(logFormater.element, level, logFormater.config, objs);
		logPrinters.forEach(p -> p.print(log));
		return this;
	}

	public void setLogFormater(LogFormater logFormater) {
		this.logFormater = logFormater;
	}

	public Logger setLogLevel(LogLevel logLevel) {
		this.logLevel = logLevel;
		return this;
	}

	public Logger warn(Object... objs) {
		return log(LogLevel.WARN, objs);
	}
}
