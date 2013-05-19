package com.abiola.bankScraper;

public class XvfbService {
	def process
	private static XvfbService instance;

	private XvfbService(){
		process = null;
	}

	public static getInstance(){
		if( instance == null ){
			instance = new XvfbService()
		}
		return instance;
	}

	def start(){
		if( process != null ){
			process.stop()
		}
		process = new ProcessBuilder("Xvfb", ':1', '-screen', '0', '1024x768x24').start()
	}

	def stop(){
		if ( process != null ){
			process.destroy()
			process = null
		}
	}
}