package com.majesteye.skeye.skeyepredict.eedaserver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class ServerApplication {

	public static void main(String[] args) {
		log.info("STARTING THE SERVER");
//		checkArgs(args);
		SpringApplication.run(ServerApplication.class, args);
		log.info("SERVER STARTED");
	}

//	private static void checkArgs(String[] args) {
//		CommandLineOptions cmdLineOptions = new CommandLineOptions();
//		CmdLineParser cmdLineParser = new CmdLineParser(cmdLineOptions);
//		try {
//			cmdLineParser.parseArgument(args);
//		} catch (CmdLineException e) {
//			log.error(e.getMessage(), e);
//			cmdLineParser.printUsage(System.err);
//		}
//	}

}
