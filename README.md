# simple-planning-tool
Simple custom planning tool for work site and worker planning

This application is to help people get an overview of the all the different sites, the workers and when those sites should start. 
The tool also helps planning in sites and how is going to work on those sites. 


## AI learnings during this project
- Be careful with JPA relations. I think you should also specify them before you start or be ready to adjust manually. 
- Time after time you have to say that you are in control and that Claude should never run any tests or version control. Put it in the CLAUDE.md file
- Jackson marshalling drops the milliseconds in string repesentations of timestamps. This is standard, but when comparing string literals, this is confusing. 
