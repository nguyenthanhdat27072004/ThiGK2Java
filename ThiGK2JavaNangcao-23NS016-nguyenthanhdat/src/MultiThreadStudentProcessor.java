import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class MultiThreadStudentProcessor {

    public static void main(String[] args) {
        final List<Student> students = new ArrayList<>();
        final CountDownLatch latch = new CountDownLatch(1);

        Thread thread1 = new Thread(() -> {
            students.addAll(parseStudentFromXML("C:\\Users\\admin\\Desktop\\JAVA.INTELIJI\\BTgiuaky2\\ThiGK2JavaNangcao-23NS016-nguyenthanhdat\\src\\Infostudent.xml"));
            latch.countDown();
        });

        Thread thread2 = new Thread(() -> {
            try {
                latch.await();
                students.forEach(student -> {
                    int age = student.tinhTuoi();
                    String mahoaTuoi = student.mahoaTuoi(age);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
            writeXML(students, "C:\\Users\\admin\\Desktop\\JAVA.INTELIJI\\BTgiuaky2\\ThiGK2JavaNangcao-23NS016-nguyenthanhdat\\src\\kq.xml");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static List<Student> parseStudentFromXML(String filePath) {
        List<Student> students = new ArrayList<>();
        File xmlFile = new File(filePath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("Student");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Element eElement = (Element) nList.item(temp);
                String id = eElement.getElementsByTagName("Id").item(0).getTextContent();
                String name = eElement.getElementsByTagName("Name").item(0).getTextContent();
                String address = eElement.getElementsByTagName("Address").item(0).getTextContent();
                String dob = eElement.getElementsByTagName("DateOfBirth").item(0).getTextContent();
                LocalDate dateOfBirth = LocalDate.parse(dob, DateTimeFormatter.ISO_LOCAL_DATE);
                students.add(new Student(id, name, address, dateOfBirth));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return students;
    }


    private static int sumDigits(int number) {
        int sum = 0;
        while (number > 0) {
            sum += number % 10;
            number /= 10;
        }
        return sum;
    }

    private static void writeXML(List<Student> students, String filePath) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();
            Element rootElement = doc.createElement("class");
            doc.appendChild(rootElement);

            for (Student student : students) {
                Element studentElement = doc.createElement("student");
                rootElement.appendChild(studentElement);
                studentElement.setAttribute("id", student.getId());
                appendChildElement(doc, studentElement, "name", student.getName());
                appendChildElement(doc, studentElement, "address", student.getAddress());
                appendChildElement(doc, studentElement, "dob", student.getDob().toString());
                appendChildElement(doc, studentElement, "age", String.valueOf(student.tinhTuoi()));
                appendChildElement(doc, studentElement, "encodedAge", student.getEncodedAge()); // Updated
                appendChildElement(doc, studentElement, "isDigitPrime", String.valueOf(student.getIsDigitPrime())); // Updated
            }


            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filePath));
            transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void appendChildElement(Document doc, Element parent, String tagName, String textContent) {
        Element child = doc.createElement(tagName);
        child.appendChild(doc.createTextNode(textContent));
        parent.appendChild(child);
    }
}
