#!/usr/bin/python
from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.chrome.service import Service
from selenium_stealth import stealth
from selenium.webdriver.common.action_chains import ActionChains
from selenium.webdriver.support.wait import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.by import By
import time
import random
import os
import glob
import shutil


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
                self.driver.get("https://www.tiktok.com/upload")
        def login(self,username,password):
                self.driver.get("https://www.tiktok.com/login")
                self.wait()
                self.clickText("Use phone / email / username")
                self.clickText("Log in with email or username")
                self.sendTextByName(username, "email")
                self.sendTextByName(password, "password")
                self.clickByClassName("login-button-31D24")

        def sendTextByName(self, text, name):
                self.wait()
                self.driver.find_element_by_name(name).send_keys(text)
        def moveMouseByXpath(self, xpath):
                self.wait()
                caption = self.driver.find_element_by_xpath(xpath)
                ActionChains(self.driver).move_to_element(caption).click(caption).perform()

        def clickText(self,string):
                self.wait()
                try:
                        self.driver.find_element_by_xpath(f"//*[text()[contains(., '{string}')]]").click()
                        return True
                except:
                        return False
        def clickByClassName(self, classname):
                self.wait()
                WebDriverWait(self.driver, 300).until(EC.element_to_be_clickable((By.CLASS_NAME, classname))).click()
        def sendTextByXpath(self, xpath,text):
                self.wait()
                WebDriverWait(self.driver, 10).until(EC.presence_of_element_located((By.XPATH, xpath))).send_keys(text)
        def selectIframe(self):
                self.wait()
                iframe = WebDriverWait(self.driver, 10).until(EC.presence_of_element_located((By.XPATH, "//html/body/div/div/div[2]/div/iframe")))
                self.driver.switch_to.frame(iframe)
        def wait(self):
                time.sleep(random.randint(1, 3))
        def waitUntilXpath(self,xpath):
                WebDriverWait(self.driver, 10).until(EC.presence_of_element_located((By.XPATH, xpath)))
        def upload(self,video_path, tags_path):
                self.driver.get("https://www.tiktok.com/upload")
                self.selectIframe()
                self.sendTextByXpath(f"//input[@type='file']",video_path )

                with open(tags_path, "r") as file:
                        tags = file.readlines()
                        self.sendTextByXpath("//html/body/div[1]/div/div/div/div/div[2]/div[2]/div[1]/div/div[1]/div[2]/div/div[1]/div/div/div/div/div/div", tags[0])

                self.clickByClassName("css-15xm9lp")
                self.waitUntilXpath("/html/body/div[3]/div/div/div[1]/div[2]")
def main():
        #username = os.environ['tiktokusername']
        #password = os.environ['tiktokpassword']
        tiktok = TiktokUploader()
        #tiktok.login(username,password)
        basedir = "/home/pablo/tiktok"

        videolist = glob.glob(basedir + "/composed_videos/*")
        for video in videolist:
                try:
                        tiktok.upload(video,"tags.txt")
                        shutil.move(video, basedir+"/uploaded/")
                except:
                        print("Failed uploading video: "+ video)
main()
