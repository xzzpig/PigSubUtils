package com.github.xzzpig.pigutils.logger;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.reflect.AnnotatedElement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.xzzpig.pigutils.json.JSONObject;

/**
 * 将objs->toString为%log%并按 {@link StringFormatTemplate#value()}(优先) 或
 * {@link JSONObject}.optString("template") 格式化
 */
public class StringLogFormater extends LogFormater {

	@Documented
	@Retention(RUNTIME)
	public static @interface StringFormatTemplate {
		/**
		 * 允许参数:<br/>
		 * <ul>
		 * <li>%log%:log内容</li>
		 * <li>%level%:Level</li>
		 * <li>%obj[i]%:第i个obj->toString()</li>
		 * <li>%date(str)%:使用当前时间格式化str</li>
		 * </ul>
		 * <br/>
		 * 默认:[%date(yyyy-MM-dd HH:mm:ss)%] [%level%] %log% <br/>
		 * 
		 * @return 格式化模板
		 */
		String value() default "[%date(yyyy-MM-dd HH:mm:ss)%] [%level%] %log%\n";
	}

	public StringLogFormater() {
	}

	@Override
	public String format(AnnotatedElement element, LogLevel level, JSONObject config, Object... objs) {
		String str = "[%date(yyyy-MM-dd HH:mm:ss)%] [%level%] %log%\n";
		if (element != null && element.isAnnotationPresent(StringFormatTemplate.class))
			str = element.getAnnotation(StringFormatTemplate.class).value();
		else if (config != null && config.has("template")) {
			str = config.optString("template", str);
		}
		StringBuilder log = null;
		if (objs != null) {
			log = new StringBuilder();
			for (int i = 0; i < objs.length; i++) {
				if (i == 0)
					log.append(objs[i]);
				else
					log.append(' ').append(objs[i]);
				str = str.replace("%obj[" + i + "]%", objs[i] + "");
			}
		}
		str = str.replace("%log%", log + "").replace("%level%", level.getName());
		String pattern = ".*%date\\((.*)\\)%.*";
		Pattern r = Pattern.compile(pattern, Pattern.DOTALL);
		Matcher m = r.matcher(str);
		Date date = new Date(System.currentTimeMillis());
		while ((m = r.matcher(str)).matches()) {
			for (int i = 1; i <= m.groupCount(); i++) {
				String rows = m.group(i);
				SimpleDateFormat dateFormat = new SimpleDateFormat(rows);
				str = str.replace("%date(" + rows + ")%", dateFormat.format(date));
			}
		}
		return str;
	}

	@Override
	public String getName() {
		return "String";
	}

	@Override
	public boolean march(AnnotatedElement element, Object... objs) {
		return true;
	}
}
