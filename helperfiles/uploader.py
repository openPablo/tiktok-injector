#!/usr/bin/python
from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.chrome.service import Service
from selenium_stealth import stealth
import time
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
import random

class TiktokUploader():
        def __init__(self):
                options = Options()
                options.add_argument("start-maximized")
                options.add_experimental_option("excludeSwitches", ["enable-automation"])
                options.add_experimental_option('useAutomationExtension', False)
                options.add_argument('user-data-dir=/home/pablo/.config/google-chrome/Profile 1')
                s = Service('/usr/bin/chromedriver')
                self.driver = webdriver.Chrome(service=s, options=options)
                stealth(self.driver,
                        languages=["en-US", "en"],
                        vendor="Google Inc.",
                        platform="Win32",
                        webgl_vendor="Intel Inc.",
                        renderer="Intel Iris OpenGL Engine",
                        fix_hairline=True,
                        )
        def login(self,username,password):
                self.driver.get("https://www.tiktok.com/login")
                self.wait()
                self.clickText("Use phone / email / username")
                self.clickText("Log in with email or username")
                self.sendTextByName(username, "email")
                self.sendTextByName(password, "password")
                self.clickLoginButton()

        def sendTextByName(self, text, name):
                self.wait()
                self.driver.find_element_by_name(name).send_keys(text)
        def upload(self):
                self.driver.get("https://www.tiktok.com/upload")

        def clickText(self,string):
                self.wait()
                try:
                        self.driver.find_element_by_xpath(f"//*[text()[contains(., '{string}')]]").click()
                        return True
                except:
                        return False
        def clickLoginButton(self):
                self.wait()
                self.driver.find_element_by_class_name(f"login-button-31D24").click()

        def wait(self):
                time.sleep(random.randint(1, 3))

tiktok = TiktokUploader()
#tiktok.login("pablo.hendrickx.tiktok@gmail.com","rvp3urUPQaauT2f!")
tiktok.upload()