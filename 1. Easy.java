import org.springframework.context.annotation.*;

class Course {
    private String courseName;
    private int duration;

    public Course(String courseName, int duration) {
        this.courseName = courseName;
        this.duration = duration;
    }

    public String toString() {
        return "Course{name='" + courseName + "', duration=" + duration + " months}";
    }
}

class Student {
    private String name;
    private Course course;

    public Student(String name, Course course) {
        this.name = name;
        this.course = course;
    }

    public void displayDetails() {
        System.out.println("Student: " + name);
        System.out.println("Enrolled in: " + course);
    }
}

class AppConfig {
   
    public Course course() {
        return new Course("Java Full Stack", 6);
    }

    public Student student() {
        return new Student("Alice", course());
    }
}

public class MainApp {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        Student student = context.getBean(Student.class);
        student.displayDetails();
        context.close();
    }
}
