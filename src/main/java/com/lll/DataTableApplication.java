package com.lll;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import cn.hutool.extra.spring.SpringUtil;

@SpringBootApplication
@Import(SpringUtil.class)
public class DataTableApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataTableApplication.class, args);
	}

}
