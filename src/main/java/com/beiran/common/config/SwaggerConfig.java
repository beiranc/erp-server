package com.beiran.common.config;

import com.beiran.security.config.JWTConfig;
import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * Swagger 配置类
 */

@Configuration
@EnableSwagger2
public class SwaggerConfig {
	
	@Bean
	public Docket createRestApi() {
		// 加入 token
		ParameterBuilder parameterBuilder = new ParameterBuilder();
		List<Parameter> parameters = new ArrayList<>();
		parameterBuilder.name(JWTConfig.tokenHeader)
				.description("token")
				.defaultValue(JWTConfig.tokenPrefix)
				.required(true)
				.modelRef(new ModelRef("string"))
				.parameterType("header")
				.build();
		parameters.add(parameterBuilder.build());
		return new Docket(DocumentationType.SWAGGER_2)
				.apiInfo(apiInfo())
				.select()
        		.apis(RequestHandlerSelectors.any())
        		.paths(Predicates.not(PathSelectors.regex("/error.*")))
        		.build()
        		.globalOperationParameters(parameters);
	}
	
	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
				.title("ERP-SERVER 接口文档")
				.version("1.1.0")
				.build();
	}
}
