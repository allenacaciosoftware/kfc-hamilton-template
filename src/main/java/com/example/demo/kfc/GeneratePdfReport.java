package com.example.demo.kfc;

import com.example.demo.repository.Child;
import com.example.demo.repository.ChildRepository;
import com.example.demo.repository.Parent;
import com.example.demo.repository.ParentRepository;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

public class GeneratePdfReport {

    public ByteArrayInputStream generateKfcReport(ResourceLoader resourceLoader, Integer childId, ChildRepository childRepository, ParentRepository parentRepository) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Optional<Child> childOptional = childRepository.findById(childId.longValue());
        if (!childOptional.isPresent()) {
            throw new IllegalArgumentException("Invalid child id:"+childId);
        }
        Child child = childOptional.get();

        Optional<Parent> parentOptional = parentRepository.findById(child.getPtrParent());
        if (!parentOptional.isPresent()) {
            throw new IllegalArgumentException("Invalid parent id:"+child.getPtrParent());
        }
        Parent parent = parentOptional.get();

        try {
            Resource resource = resourceLoader.getResource(
                    "classpath:images/kfc-logo-438x80.png");
            if (resource.exists()) {
                // do one thing
                System.out.println("pdf resource exists...");
            } else {
                // do something else
                System.out.println("pdf resource does not exists...");
            }

            URL myurl = resource.getURL();
            Image image = Image.getInstance(myurl);

            PdfWriter.getInstance(document, out);

            document.open();
            String title = "Information sheet & parents’ consent (one sheet per child)";
            Font f = new Font();
            f.setStyle(Font.BOLD);
            f.setSize(18);

            document.add(image);
            Paragraph p = new Paragraph(title, f);
            p.setAlignment(Element.ALIGN_CENTER);
            document.add(p);


            printChildInfo(document, child);
            printActivities(document);
            printModeOfCommunication(document, parent);
            printParentSignature(document, parent);

            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return new ByteArrayInputStream(out.toByteArray());
    }

    private PdfPCell addToCell(String text) {
        return addToCell(text, false);
    }

    private PdfPCell addToCell(String text, boolean noBorder) {
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setPadding(5.0f);
        if (noBorder) {
            cell.setBorder(Rectangle.NO_BORDER);
        }
        return cell;
    }

    private void addSubTitle(Document document, String subtitle) throws DocumentException {
        Font f = new Font();
        f.setStyle(Font.BOLD);
        f.setSize(12);
        Paragraph paragraph = new Paragraph(subtitle, f);
        paragraph.setSpacingBefore(20f);
        document.add(paragraph);

    }

    private String getDefaultText(String text) {
        return StringUtils.isEmpty(text) ? "N/A" : text.trim();
    }

    private void printParentSignature(Document document, Parent parent) throws DocumentException {
        String underline = "_______________________";

        PdfPTable table = new PdfPTable(4);
        float[] columnWidths = new float[]{10f, 25f, 10f, 25f};
        table.setWidths(columnWidths);
        table.setSpacingBefore(30f);
        table.setWidthPercentage(100f);


        String[] fullnames = parent.getFullName().split(",");

        for (int i=0; i<fullnames.length; i++) {
            table.addCell(addToCell("Full name:", true));
            table.addCell(addToCell(getDefaultText(fullnames[i]), true));
            table.addCell(addToCell("Signature:", true));
            table.addCell(addToCell(getDefaultText(underline), true));
        }

        table.addCell(addToCell("", true));table.addCell(addToCell("", true));table.addCell(addToCell("Date:", true));table.addCell(addToCell(getDefaultText(underline), true));
        document.add(table);
    }

    private void printModeOfCommunication(Document document, Parent parent) throws DocumentException {
        addSubTitle(document, "Mode of Communication");
        PdfPTable table = new PdfPTable(2);
        table.setSpacingBefore(10f);
        table.setWidthPercentage(100f);

        String email = parent.getEmail();
        String mobileNo = parent.getMobileNo();
        String facebook = parent.getFacebookAccount();

        table.addCell(addToCell("Email"));table.addCell(addToCell(getDefaultText(email)));
        table.addCell(addToCell("Mobile Number"));table.addCell(addToCell(getDefaultText(mobileNo)));
        table.addCell(addToCell("Facebook"));table.addCell(addToCell(getDefaultText(facebook)));
        document.add(table);

        Font f = new Font();
        f.setSize(12);
        String agreement = "I agree and give consent to use the above mode of communication/s for CFC Kids for Christ";
        agreement += "\n" + "ministry to disseminate and announce its activities, schedules, and formations.";
        Paragraph paragraph = new Paragraph(agreement, f);
        paragraph.setSpacingBefore(10f);
        document.add(paragraph);
    }

    private void printActivities(Document document) throws DocumentException {
        addSubTitle(document, "Activities for Kids for Christ");

        PdfPTable table = new PdfPTable(3);
        table.setSpacingBefore(10f);
        table.setWidthPercentage(100f);

        table.addCell(addToCell("Prayer meetings"));table.addCell(addToCell("Reach out program"));table.addCell(addToCell("Kids day"));
        table.addCell(addToCell("Mass choir"));table.addCell(addToCell("Kids camp"));table.addCell(addToCell("Kids conference"));
        table.addCell(addToCell("Family day"));table.addCell(addToCell("Parent-child activity"));table.addCell(addToCell("Sacrament celebration"));
        document.add(table);
    }


    private void printChildInfo(Document document, Child child) throws DocumentException {
        addSubTitle(document, "Child Information");

        PdfPTable table = new PdfPTable(2);
        table.setSpacingBefore(10f);
        table.setWidthPercentage(100f);

        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        String childName = child.getFull_name();
        String perferredName = child.getPreferred_name();
        String gender = child.getGender();
        List allergies = Arrays.asList(child.getAllergies().split(","));
        List medicalNeeds = Arrays.asList(child.getMedical_needs().split(","));
        List hobbies = child.getHobbies() == null ? Collections.emptyList() : Arrays.asList(child.getHobbies().split(","));

        table.addCell(addToCell("Name of Child: ")); table.addCell(addToCell(childName));
        table.addCell(addToCell("Preferred Name: " )); table.addCell(addToCell(perferredName));
        table.addCell(addToCell("Date of Birth: " )); table.addCell(addToCell(child.getBirthDateHumanReadable()));
        table.addCell(addToCell("Date of Baptism: " )); table.addCell(addToCell(child.getBaptismDateHumanReadable()));
        table.addCell(addToCell("Date of First Communion: ")); table.addCell(addToCell(child.getFirstCommunionDateHumanReadable()));
        table.addCell(addToCell("Date of Confirmation: " )); table.addCell(addToCell(child.getConfirmationDateHumanReadable()));
        table.addCell(addToCell("Gender: " )); table.addCell(addToCell(gender));
        table.addCell(addToCell("Allergies: ")); table.addCell(addToCell(allergies.isEmpty() ? "N/A" : StringUtils.join(allergies, ",")));
        table.addCell(addToCell("Medical Needs: ")); table.addCell(addToCell(medicalNeeds.isEmpty() ? "N/A" : StringUtils.join(medicalNeeds, ",")));
        table.addCell(addToCell("Hobbies: " )); table.addCell(addToCell(hobbies.isEmpty() ? "N/A" : StringUtils.join(hobbies, ",")));

        document.add(table);

    }

}
