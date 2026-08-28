package com.cultivaplus.AEP_6S;

import org.springframework.boot.SpringApplication;

public class TestAep6SApplication {

	public static void main(String[] args) {
		SpringApplication.from(Aep6SApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
