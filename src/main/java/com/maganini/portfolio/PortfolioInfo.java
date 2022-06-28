package com.maganini.portfolio;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Data
public class PortfolioInfo {
    public PortfolioInfo() {
        name = "Michael Maganini";
        currJobTitle = "Java Full Stack Developer";
        currLocation = "Plano, Texas, United States";
        bio = "I am a Java Full Stack Developer at GenSpark and recently graduated from Purdue " +
                "University with a B.S. degree in Computer Science. I have always been passionate about software development " +
                "and am experienced with Java development. My passion comes from the satisfaction that comes from solving " +
                "engineering problems in development and from how I enjoy learning new technologies. My hobbies include " +
                "running, skiing, travelling, meditation, cryptocurrency, and gaming. GitHub: https://github.com/mmaganin";
        education = new Education("Purdue University", "Bachelors of Science",
                "Computer Science with a concentration in Software Engineering", "Jul 2017", "Dec 2021",
                "Relevant courses: Computer Security, Software Engineering Senior Project, Information Systems, " +
                        "Software Testing, Software Engineering I, Systems Programming, Computer Architecture, Data Structures And Algorithms, Java OOP Programming");
        try {
            industryExperiences = PortfolioListDetails.generateList(IndustryExperience.class);
            certificates = PortfolioListDetails.generateList(Certificate.class);
            licenses = PortfolioListDetails.generateList(License.class);
            skills = PortfolioListDetails.generateList(Skill.class);
            projects = PortfolioListDetails.generateList(Project.class);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    //INFO FROM LINKEDIN PAGE
    public final String name;
    public final String currJobTitle;
    public final String currLocation;
    public final String bio;
    public final Education education;
    public final List<Object> industryExperiences;
    public final List<Object> certificates;
    public final List<Object> licenses;
    public final List<Object> skills;
    public final List<Object> projects;


    public static void main(String[] args) {
        PortfolioInfo portfolioInfo = new PortfolioInfo();
        System.out.println(portfolioInfo);
    }
}

class PortfolioListDetails {
    //Individual IndustryExperience entries
    public static final IndustryExperience industryExperience1 =
            new IndustryExperience("Java Full Stack Developer", "GenSpark", true, "Mar 2022", "Present");
    //Individual Certificate entries
    public static final Certificate certificate1 =
            new Certificate("Java Developer Certificate", "GenSpark", "Mar 2022", "None");
    //Individual License entries
    //Individual Skill entries
    public static final Skill skill1 = new Skill("JavaScript");
    public static final Skill skill2 = new Skill("Java");
    public static final Skill skill3 = new Skill("React.js");
    public static final Skill skill4 = new Skill("Amazon Web Services (AWS)");
    public static final Skill skill5 = new Skill("Spring Boot");
    public static final Skill skill6 = new Skill("HTML");
    public static final Skill skill7 = new Skill("Cascading Style Sheets (CSS)");
    public static final Skill skill8 = new Skill("Hibernate");
    public static final Skill skill9 = new Skill("JUnit");
    public static final Skill skill10 = new Skill("MySQL");
    //Individual Project entries
    public static final Project project1 = new Project("Crypto Markets and Portfolio Tracker App",
            "May 2022", "May 2022", "GenSpark",
            "React, Spring Boot, MySQL, AWS web application developed during my GenSpark training. It pulls " +
                    "market data from coinmarketcap.com, allows user accounts to view the markets and add cryptocurrency to their portfolio.");
    public static final Project project2 = new Project("Purdue App Team Senior Project",
            "Aug 2021", "Dec 2021", "Purdue University",
            "React, Redux, Firebase web application for Purdue students that includes a class forum, " +
                    "marketplace, clubs and events pages, and other features. Developed on a team for my senior project at Purdue University.");
    public static final Project project3 = new Project("Student Scheduler App Purdue Team Project",
            "Aug 2021", "Dec 2021", "Purdue University",
            "Student scheduler Flask, SQLAlchemy app to practice using ORM, prepared statements, different " +
                    "transaction levels, and a UI for the user to query the database. Developed in the Information Systems Purdue course.");
    public static final Project project4 = new Project("Boarding Pass App Team Project",
            "", "", "GenSpark",
            "A Spring Boot, React, MySQL full stack web application I developed on a team during my GenSpark " +
                    "training that we developed in a week's time. It generates a boarding pass based on a customer's " +
                    "inputted info and generates a boarding pass for a selected NY subway system (from MTA API).");
    public static final Project project5 = new Project("Humans Vs. Goblins Java GUI game",
            "", "", "GenSpark",
            "A Java Swing GUI game that allows players to fight goblins and open treasure chests on a grid " +
                    "field. The game has an inventory and stats system where items dropped from goblins and obtained from " +
                    "chests alter a players inventory and stats. Used JUnit testing.");

    public static List<Object> generateList(Class portfolioClass) throws IllegalAccessException {
        String className = portfolioClass.getSimpleName();
        String varName = className.replaceFirst("" + className.charAt(0), ("" + className.charAt(0)).toLowerCase());


        ArrayList<Object> list = new ArrayList<>();
        Field field;
        int i = 1;
        try {
            while (true) {
                field = PortfolioListDetails.class.getField(varName + i);
                list.add(field.get(portfolioClass));
                i++;
            }
        } catch (NoSuchFieldException e) {
            return list;
        }
    }
}

