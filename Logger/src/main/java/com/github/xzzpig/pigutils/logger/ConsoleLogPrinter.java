package com.github.xzzpig.pigutils.logger;

public class ConsoleLogPrinter extends OutputStreamLogPrinter {

	public ConsoleLogPrinter() {
		super(System.out);
	}

	@Override
	public String getName() {
		return "Console";
	}

}
