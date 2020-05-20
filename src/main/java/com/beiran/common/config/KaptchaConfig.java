package com.beiran.common.config;

import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * 	验证码配置类<br>
 * 	kaptcha 常用的常量见 com.google.code.kaptcha.Constants<br>
 * 	kaptcha.border 是否有边框，默认为 true，可选值 yes/no<br>
 * 	kaptcha.border.color 边框颜色，默认为 Color.BLACK<br>
 * 	kaptcha.border.thickness 边框粗细度，默认为 1<br>
 * 	kaptcha.producer.impl 验证码生成器，默认为 DefaultKaptcha<br>
 * 	kaptcha.textproducer.impl 验证码文本生成器，默认为 DefaultTextCreator<br>
 * 	kaptcha.textproducer.char.string 验证码文本字符内容范围，默认为 abcde2345678gfynmnpwwx<br>
 * 	kaptcha.textproducer.char.length 验证码文本字符长度，默认为 5<br>
 * 	kaptcha.textproducer.font.names 验证码文本字体样式，默认为 Arial, Courier<br>
 * 	kaptcha.textproducer.font.size 验证码文本字符大小，默认为 40<br>
 * 	kaptcha.textproducer.font.color 验证码文本字符颜色，默认为 Color.BLACK<br>
 * 	kaptcha.textproducer.char.space 验证码文本字符间距，默认为 2<br>
 * 	kaptcha.noise.impl 验证码噪点生成对象，默认为 DefaultNoise<br>
 * 	kaptcha.noise.color 验证码噪点颜色，默认为 Color.BLACK<br>
 * 	kaptcha.obscurificator.impl 验证码样式引擎，默认为 WaterRipple<br>
 * 	kaptcha.word.impl 验证码文本字符渲染，默认为 DefaultWordRenderer<br>
 * 	kaptcha.background.impl 验证码背景生成器，默认为 DefaultBackground<br>
 * 	kaptcha.background.clear.from 验证码背景颜色渐进，默认为 Color.LIGHT_GRAY<br>
 * 	kaptcha.background.clear.to 验证码背景颜色渐进，默认为 Color.WHITE<br>
 * 	kaptcha.image.width 验证码图片宽度，默认为 200<br>
 * 	kaptcha.image.height 验证码图片高度，默认为 50<br>
 * 	kaptcha.session.key kaptcha 的 Session key 名字，默认为 KAPTCHA_SESSION_KEY<br>
 * 	kaptcha.session.date kaptcha 的 Session Date，默认为 KAPTCHA_SESSION_DATE<br>
 */

@Configuration
public class KaptchaConfig {

	@Bean
	public DefaultKaptcha producer() {
		// 配置 kaptcha 的属性
		Properties properties = new Properties();

		properties.put(Constants.KAPTCHA_BORDER, "no");
		properties.put(Constants.KAPTCHA_TEXTPRODUCER_FONT_COLOR, "black");
		properties.put(Constants.KAPTCHA_TEXTPRODUCER_CHAR_SPACE, "5");
		properties.put(Constants.KAPTCHA_TEXTPRODUCER_CHAR_LENGTH, "4");
		properties.put(Constants.KAPTCHA_SESSION_CONFIG_KEY, "code");
//		properties.put(Constants.KAPTCHA_TEXTPRODUCER_CHAR_STRING, "0123456789ABCEFGHIJKLMNOPRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
		properties.put(Constants.KAPTCHA_TEXTPRODUCER_CHAR_STRING, "abcde2345678gfynmnpwwx");
		properties.put(Constants.KAPTCHA_OBSCURIFICATOR_IMPL, "com.google.code.kaptcha.impl.WaterRipple");
		properties.put(Constants.KAPTCHA_IMAGE_WIDTH, "111");
		properties.put(Constants.KAPTCHA_IMAGE_HEIGHT, "36");
		properties.put(Constants.KAPTCHA_TEXTPRODUCER_FONT_SIZE, "30");

		Config config = new Config(properties);
		DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
		defaultKaptcha.setConfig(config);
		return defaultKaptcha;
	}
}
