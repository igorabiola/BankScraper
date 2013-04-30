import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxBinary
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import com.gargoylesoftware.htmlunit.BrowserVersion

driver = { 

	//new FirefoxDriver()
	//new HtmlUnitDriver(BrowserVersion.FIREFOX_17)
	/* Xvfb :1 -screen 0 1024x768x24 */
	//new ProcessBuilder("Xvfb", ':1', '-screen', '0', '1024x768x24').start();
	XvfbService.getInstance().start()

	FirefoxBinary firefoxBinary = new FirefoxBinary();
	firefoxBinary.setEnvironmentProperty('DISPLAY', ':1')
	return new FirefoxDriver(firefoxBinary, null);

}

reportsDir = "build/geb-reports"