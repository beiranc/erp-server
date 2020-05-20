package com.beiran;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * TODO: 添加文件上传功能（主要是上传头像）
 */

@ComponentScan({ "com.beiran" })
@EntityScan("com.beiran")
@EnableJpaRepositories("com.beiran")
@EnableAspectJAutoProxy
@EnableTransactionManagement
@SpringBootApplication
public class MainConfig {

	public static void main(String[] args) {
		SpringApplication.run(MainConfig.class, args);
	}

}
