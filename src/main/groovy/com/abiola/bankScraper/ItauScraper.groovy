package com.abiola.bankScraper

import org.apache.log4j.Logger
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;

import geb.Browser
import geb.Page;

class HomePage extends Page{
	static url = "https://bankline.itau.com.br/lgnet/itauf/bankline.htm"	
	static at = { title == "Banco Itaú - Feito Para Você" }
	static content = {
		branch(wait: true) { $('input', id: 'agencia') }
		account { $('input', id: 'conta') }
		verifier { $('input', id: 'dac') }
		submit(to:[FailPage, LoginPage]) { $('div', id: 'btnEnviar').find("input") }	
	}
}

class FailPage extends Page{
	static at = {
			$('span', class: 'msgtxt') != null
			$('span', class: 'msgtxt').text() == 'Agência/Conta inválida. Verifique se o número digitado está correto.' 
	}	
}


class LoginPage extends Page{
	static at = { 
		$('form', name: 'bankline') != null
		$('form', name: 'bankline').size() > 0
	}
	
	static content = {
		clientName { $('div', id: 'MSGBordaEsq').find("a", class: 'msgtexto8') }
		form { $('form', name: 'bankline') }	
		buttons { form.find('td', class: 'textotecladovar') }
		submit(to:WelcomePage) { form.find('a', id: 'idBtnContinuar') }
	}
}

class WelcomePage extends Page{
	static at = { 
		$('div', id:'HObemvindo01').find('span', class:'HOsaudacao').text().startsWith("Boa") ||
		$('div', id:'HObemvindo01').find('span', class:'HOsaudacao').text().startsWith("Bom")
	}
	static content = {
		statmentDiv { $('div', id: 'HOposresumida') }
		statmentLink(to: StatmentPage ) { statmentDiv.find('a', title: 'ver extrato') }	
		balance { statmentDiv.find('span', 2).text() }
		limit { statmentDiv.find('span', 4).text() }
	}
}

class StatmentPage extends Page{
	static at = { 
		$('div', id: 'menuatalho').find('a', class: 'menuatalhoitem', title:'ver extrato').size() > 0	
	}
	static content = {
		optionsBox { $('div', id: 'BOXcontainer01') }
		statmentLink(to: StatmentExportMiscPage ){ optionsBox.find( 'p', 4 ).find('a') }
	}	
}

class StatmentExportMiscPage extends Page{
	static at = {
			$('form').find('h1').text() == "Geração de arquivo em outros formatos"
	}
	static content = {
			form{ $('form', name:'Form1') }
			submit{ $('.TRNbotao').find('a') }
			postLink { form.@action }
	}	
}


class ItauScraper {
	def browser = null
	def branch
	def account
	def verifier
	def password
	def saveDir
	Date lastSync = null
	
	def keys = null;
	Logger logger = Logger.getLogger(this.class);
	
	def mapKeys( buttons ){
		keys = [:]
		buttons.each{ 
			def digits = it.text() =~ /(\d) ou (\d)/
			def link = it.find("a")
			keys.put(digits[0][1], link)
			keys.put(digits[0][2], link)
		}
		return keys
	}
	
	def downloadStatment( browser ){
		browser.downloadStream{  HttpURLConnection connection ->
			def urlParameters = "id="+browser.form.id
			urlParameters+= "&op="+browser.form.op
			urlParameters+= "&Dia="+browser.form.Dia
			urlParameters+= "&Mes="+browser.form.Mes
			urlParameters+= "&Ano="+browser.form.Ano
			urlParameters+= "&OP1="+browser.form.OP1
			
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
			connection.setDoInput(true);
			connection.setDoOutput(true);
			
			DataOutputStream wr = new DataOutputStream (connection.getOutputStream ());
			wr.writeBytes (urlParameters);
			
		}
	}
	
	def fillForm( browser, Date lastSync ){
		
		def day;
		def month;
		def year;
		if( lastSync != null ){
			def start = lastSync + 1
			day = start.format('dd')
			month = start.format('MM')
			year = start.format('yyyy')
		}else{
			def start = new Date();
			day = 01;
			month = start.format('MM')
			year = start.format('yyyy')
		}
		
		
		browser.form.Dia = day
		browser.form.Mes = month
		browser.form.Ano = year
		browser.form.OP1 = "OFX"
		
	}
	
	def scrape = {
		browser.to HomePage
		if(browser.at(HomePage)){
			browser.branch << branch
			browser.account << account
			browser.verifier << verifier
			browser.submit.click()
			if ( browser.at(LoginPage) ){
				browser.clientName.click()
				keys = mapKeys(browser.buttons)
				def pass = password =~ /\d/
				pass.each{ 
					keys[it].click()
				}
				browser.submit.click()
				assert browser.at(WelcomePage)
				browser.statmentLink.click()
				assert browser.at(StatmentPage)
				browser.statmentLink.click()
				assert browser.at( StatmentExportMiscPage )
				
				fillForm( browser, lastSync )
				
				def arq = downloadStatment(browser)
				logger.info("Download complete!")
				new File(saveDir).mkdirs()
				
				def now= new Date();
				def day = now.format('dd')
				def month = now.format('MM')
				def year = now.format('yyyy')
				
				File statment = new File(saveDir, "statment_${browser.form.Ano}_${browser.form.Mes}_${browser.form.Dia}_TO_${year}_${month}_${day}.ofx")

				statment.append(arq)
				return statment.absolutePath
			}
			else{
				println "Branch/Account combination invalid!"
				return -1
			}
		}
	}
	
}






