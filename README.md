mysaasa Server Readme
Copyright 2015, Adam Hammer


mysaasa Server
A CMS/SaaS/API/Android Suite designed for customizable websites and apps.

Getting Started

1. With a clean install of Linux/OSX or Windows, perform the following
    a) Install the JDK 1.8 or higher
    b) From a command line, $PATH_TO_FOLDER/gradlew
    c

Table Of Contents
	2. FAQ
	2.1. What is this?
	2.2. Who is it for?
	2.3. Why do I want it?
	2.4. Why not X or Y?
	2.5. I want to use this for Commercial Uses, what are my options?
	2.6. How would I get started?
	2.7. What are the core building blocks.
	2.8. I want an Android App, how do I get started with that?
	2.9. I want to contribute, what can I do?
	2.10. I want support on the platform, how can I official get support?

1. Getting Started

	To Run: "./gradlew run"
    If all is good, you can access the Admin from localhost:8080

    DNS Setup:
        The system can register an arbitrary amount of domains. These domains can be linked to organizations and share data.
        For simple usage, your system hostfile can be used. However I highly recommend using a local DNS server for
        development and demonstration setup.

        The local DNS server can handle wildcard subdomains, where hostfiles can not. This allows the mechanism
        for inline website editing to work from the Admin. mysaasa uses sub domains created on the fly to link the users
        website and admin session across domains. It can also host staging environments. Given the wide range of domains
        it may wish to refer to itself as, a wildcard domain simplifies development.

	Prerequisites:
	A directory for Data and Settings.
		Default Value: 	/opt/mysaasa  on Linux/Mac 
				C:\opt/mysaasa\ on windows

	Java JDK/JRE 1.8 for Server Development/Deployment
    Java JDK/JRE 1.7 for Java Client/Android Developments
    Android SDK 4.0+ for Android Development



2. FAQ

2.1. What is this?

A Java based http server that specializes in providing organizations with the ability to have API Driven websites and Apps that are easily extensible. 

It includes 3 main components (Java 8).
	Admin interface: 
		Admin to manage Websites and Data.
	Templated Hosting Service: 
		Hosted websites use a Templating language, similar to PHP. API easily extended with Annotation based Java Bindings.
	API Hosting Service: 
		Provides a RESTFul Json API for every website. Can be used in advanced, data driven websites or as a contact point for Apps or IoT style integrations.

And 3 External components (Apache Licensed, Java 7 for Android Support)
	Java API:
		Auto-generated bindings to the API and some network logic.
	Android API:
		Components designed to work directly with the Java API
	Android White Label:
		Pre-built application to give a Website an App, can be used as the basis for a branded/customized android application.


2.2. Who is it for?

Intermediate to advanced users and developers.

Usage Only: DNS, Java (running & setup), HTML/CSS. Basic scripting knowledge will go a long way in developing website templates.
Development: Java (Beginner->Advanced), Gradle, Android experience 

On the user side, it's for power users who want multiple websites and a interface for managing/editing them.

On the developer side, it's for SaaS or API driven projects. Ideal projects would be people who want to build a CMS/App/SaaS for a industry they may be a domain expert in. This could provide foundation and could be extended to provide functionality to the domain experts knowledge.

It's also for people who just want a API driven project that may/may not have a website attached. I'm hoping to provide a special license for indepedent game developers to use this server to provide data-driven API access to their games, which is a major burden for creating network attached games.

2.3. Why do I want it?

- Because it gives you an entire Stack in a box (Server/Websites/API/Admin), and does so in a tidy and neat package designed for extension.
- There is minimal config (however some understanding of DNS is a must for the person setting up websites).
- Designed to share content across multiple websites and apps.
- Designed to support multiple organizations and keeping their information seperate.
- Provides some easy to use templates to help users get started with their first website and apps
- Open Source, with Commercial options available if your business requires it

2.4. Why not X or Y?

Because MySaasa is awesome. From one website to 100 website/app combo's, MySaasa is designed to scale to your needs.

I designed it after watching so many companies build in house solutions that don't scale, and so many people abusing
WordPress and PHP for everything web.

2.5. I want to use for Commercial Uses, what are my options?

This project can be offered under alternative licenses, please contact Adam Hammer 1(604)339-2620 to discuss licensing, development and support options that may be well suited to you.

2.6 How do I get started (Windows, Linux, OSX all supported)
Developer Getting Started
- Install Java 8 JDK 
- Check out Code
- run './gradlew run' to start application

User Getting Started
- Download Archive 
- Extract Archive
- run [EXTRACTED_FOLDER]/bin/Server to start

2.7 What are the Core building blocks.

This piece of software is built on the shoulders of other great projects

- Jetty 9.2.9 (Application server, is the backbone of the HTTP server)
- Apache Wicket 6.21 (7.x mostly supported, just needs Testing, will be updated soon)
- CKEditor (Used to provide inline WYSIWYG editing support on websites)
- ACE Code Editor (Used to provide online editing of web files and templates)
- Google GSON and Guava (Used to provide JSON serialization/deserialization for API support)
+ Many more (Check the build.gradle's for exact usages amd versions)

2.8. I want an Android App, how do I get started with that?
Step 1: Create and setup a server and website.
Step 2: Check out Android components (Link TBD)
Step 3: Create a build flavour in the White Label project
Step 4: Start extending your application

The Client tools will be released under a more permissive license, meaning you are not obliged to release or distribute the source.

2.9. I would like to contribute, what can I do?

Code contributions to the master branch will not be accepted by default, so please contact me before contributing.
I'll eventually offer an official policy for bounties on features/bugs/enhancements. 
I want to pay for all contributions to keep copyright/licensing straightforward.


2.10. I want support on the platform, how can I official get support?

	Support on the AGPL code base is subject to acceptance to the AGPL core. Otherwise, flat rates apply, bought in 10 hour increments.

	$75/hr			Priority Bugfixes/Features/Improvements to the AGPL core.
	$110/hr			Custom development for 3rd party AGPL Branch
	Contact			Non-AGPL Licensing and Development costs

	Large or enterprise level projects may be subject to a bulk discount or high priority support licenses.
	
	To Contact about licensing, Adam Hammer (adamhammer2@gmail.com) 1(604)339.2620


