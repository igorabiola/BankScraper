package com.abiola.bankScraper

import geb.Browser
import geb.Page;


class MyFinanceLoginPage extends Page{
	static url = "https://app.myfinance.com.br/users/auth/passaporte_web"
	static at = { title == "Login | Passaporte Web" }
	static content = {
		login(wait: true) { $('input', id: 'id_email') }
		password { $('input', id: 'id_password') }
		submit(to:[MyFinanceHomePage]) { $('input', id: 'submit-button') }
	}
}

class MyFinanceHomePage extends Page{
	static at = { title == "Visão Geral - Painel - MyFinance" }
	static content = {
		finances { $(id: 'js-item-entities') }
		menu { $('div', id:'js-menu-entities') }
		accounts( to:MyFinanceAccountPage ) { menu.find('a', text:'Movimento Bancário') }
	}
}

class MyFinanceAccountPage extends Page{
	static at = { title.startsWith("Conta")}
	static content = {
		switcherAccount { $(id: 'show-account-switcher') }
		accountsList { $('div', class:'accountList') }
		statmentImport{ $( id:'li-import-financial-transaction' ) }
		importFile{ $( id:'bank_statement_file' ) }
		importForm{ $(id:'bank_statement_upload') }
		importSubmit{ $(id:'bank_statement_upload').find('input', type:'submit') }
		importRefresh( required: false ){ $(id:'import_refresh_button') }
	}
	
	def refreshImport(){
		waitFor(60, 0.3){ importRefresh.displayed }
		importRefresh.click()
	}
}


class MyFinanceExporter {
	
	def login = null
	def password = null
	def accountName = null

	def browser = null
	
	def findAccountLink( String accountName, accountList ){
		accountList.find('a', title: accountName)
	}
	
	def export( String extractPath ){
			browser.to MyFinanceLoginPage
			browser.login << login
			browser.password << password
			browser.submit.click()
			
			browser.finances.click()
			browser.accounts.click()
			
			browser.switcherAccount.click()
			
			findAccountLink( accountName , browser.accountsList ).click()
			
			browser.statmentImport.click()
		
			println extractPath
			browser.importFile.value(extractPath)
				
			browser.importSubmit.click()
			
			browser.refreshImport()
	}
}
