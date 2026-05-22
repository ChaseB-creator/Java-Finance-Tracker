# Personal Finance Tracker (in Java and Java Swing)

This is a Java program that uses Java Swing that implements a Graphic User Interface (GUI), allows the user to create a account, allows the user to login, has data saved locally to that specific login, and then takes income and expenses from the user and visually represent such.

## Instructions for Build and Use

Steps to build and run the software:

1. Install Java 11 or newer and Maven.
2. Open a terminal in the project root.
3. Run `mvn package`.
4. Launch the application with:
   `java -jar target/java-finance-tracker-1.0.0-jar-with-dependencies.jar`
5. From then you can run the project by clicking on the executable file (works with Windows Machines, I'm pretty sure it won't work with other machines though).

Instructions for using the software:

1. Create a username and password on the Sign Up screen.
2. Log in using the account info created by the user.
3. Enter monthly income, monthly expenses, a savings goal, and a projection period in days.
4. Click `Calculate Projection` to update the line chart and see estimated goal timelines.
5. Click `Save To Account` to store the current finance projection for your account.

## Development Environment

To recreate the development environment, you need:

* Java 11 or newer
* Apache Maven
* JFreeChart dependency provided by Maven
* SQLite JDBC driver provided by Maven

## Useful Websites to Learn More

I used the following websites as reference material along with material to establish initial planning.

* Reading and Code Anaylsis: [https://data-flair.training/blogs/java-expense-tracker/]
* Useful GitHub Reference I used: [https://github.com/onkar69483/Personal_Finance_Management-Java-Swing]
* More reading and Code References: [https://codewithcurious.com/projects/personal-finance-manager-using-java/]
* The AI I used for establishing depedencies, connection to SQLite, Password Encrypting, and package compilation: [https://gemini.google.com/app]

## Future Work

The following items can be added later:

[] Add transaction-level income/expense entries for ledger tracking.
[] Add multiple financial goals and archived projections per user.
[] Add a stronger account dashboard with charts for monthly spending categories.
