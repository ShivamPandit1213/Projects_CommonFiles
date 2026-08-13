JENKINS:
@echo off
echo Starting Jenkins with JDK 21...

set "JAVA_HOME=C:\Program Files\Java\jdk-21.0.12"
cd /d C:\Users\shiva\OneDrive\Jenkins

"%JAVA_HOME%\bin\java" -jar jenkins.war
pause
=================================================================
  
GITHUB:
Go to github.com → New repository → name it paimana_1point1

Repository name:  paimana_1point1  ✓
Visibility:       Private          ✓ good call for MoSPI work
Add README:       Off              ✓
Add .gitignore:   No .gitignore    ✓
Add license:      No license       ✓


In CMD:
cd C:\Users\shiva\OneDrive\JavaSelenium\paimana_1point1
git remote add origin https://github.com/ShivamPandit1213/paimana_1point1.git
git branch -M main
git push -u origin main

For your other projects, the full sequence each time:

cd <project folder>
git init
git add .
git commit -m "Initial commit"
git remote add origin <url from GitHub>
git push -u origin main
=================================================================
From now on, this project's workflow is three commands:

git add .
git commit -m "what changed"
git push

No -u origin main needed again — the upstream is set.

Useful along the way:

git status              what's changed
git log --oneline       commit history
git diff                exact line changes before staging
=============================================================================================================================
Or rename to master if you prefer consistency with your other repo:
git branch -M master
git push -u origin master

Verify your branch name first if unsure:
git branch
