package com.example.demo.kfc;

import com.example.demo.Utils;
import com.example.demo.kfc.event.EventController;
import com.example.demo.repository.*;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping(MyController.CONTROLLER_URL)
public class MyController {
    private static final String CONTROLLER_PATH = "kfc/user";
    public static final String CONTROLLER_URL = "/" + CONTROLLER_PATH;

    @Autowired
    ParentRepository parentRepository;
    @Autowired
    ChildRepository childRepository;
    @Autowired
    EventController eventController;
    @Autowired
    EventRepository eventRepository;

    @GetMapping({"/",""})
    public String sendForm(@NonNull Model model) {
        return CONTROLLER_PATH + "/add-user";
    }

    private void setModel(long childId, @NonNull Model model){
        Optional<Child> optionalChild = childRepository.findById(childId);
        if (!optionalChild.isPresent()) {
            throw new IllegalArgumentException("Invalid child id: " + childId);
        }
        Child child = optionalChild.get();
        Optional<Parent> optionalParent = parentRepository.findById(child.getPtrParent());
        if(!optionalParent.isPresent()) {
            throw new IllegalArgumentException("Invalid parent id: " + child.getPtrParent() + " for child: " + child.getFull_name());
        }

        model.addAttribute("child", child);
        model.addAttribute("parent", optionalParent.get());
    }

    @GetMapping({"/editUser/{childId}"})
    public String sendForm(@PathVariable long childId, Model model) {
        setModel(childId, model);
        return CONTROLLER_PATH + "/add-user";
    }

    @GetMapping({"/viewUser/{childId}"})
    public String viewForm(@PathVariable long childId, Model model) {
        setModel(childId, model);
        return CONTROLLER_PATH + "/showMessage";
    }

    @Transactional
    @PostMapping({"/add-user"})
    public RedirectView processForm(FormInfo formInfo, Model model, RedirectAttributes redir) throws ParseException {

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date dateCreated = new Date();

            Long childId = formInfo.getChild_Id();
            boolean editUser = false;
            // If there is no id, it is adding a user.
            if (childId > 0) {
                editUser = true;
            }

            Child child = new Child();
            Parent parent = new Parent();
            if (editUser) {
                Optional<Child> childOptional = childRepository.findById(childId);
                if (!childOptional.isPresent()) {
                    throw new IllegalArgumentException("Invalid child id:" + childId);
                }
                child = childOptional.get();
                Optional<Parent> optionalParent = parentRepository.findById(child.getPtrParent());
                if (!optionalParent.isPresent()) {
                    throw new IllegalArgumentException("Invalid parent id for child: " + child.getFull_name());
                }
                parent = optionalParent.get();
            } else {
                String[] parentEmails = formInfo.getParent_email().trim().split(",");
                for (String parentEmail : parentEmails) {
                    parent = parentRepository.findByEmailContaining(parentEmail);
                    if (parent != null && parent.getId() > 0) {
                        break;
                    }
                }

                if (parent == null) {
                    parent = new Parent();
                }
            }

            parent.setFullName(formInfo.getParent_fullname().trim());
            parent.setEmail(formInfo.getParent_email().trim());
            parent.setFacebookAccount(formInfo.getParent_facebook_account().trim());
            parent.setMobileNo(formInfo.getParent_mobile_no().trim());
            parent.setCreatedDate(dateCreated);
            Parent newParent = parentRepository.save(parent);
            System.out.println("newParent:::" + newParent);

            child.setFull_name(formInfo.getChild_fullname().trim());
            child.setPreferred_name(formInfo.getChild_preferred_name().trim());
            child.setBirth_date(StringUtils.isEmpty(formInfo.getChild_birth_date()) ? null : simpleDateFormat.parse(formInfo.getChild_birth_date()));
            child.setBaptism_date(StringUtils.isEmpty(formInfo.getChild_baptism_date()) ? null : simpleDateFormat.parse(formInfo.getChild_baptism_date()));
            child.setFirst_communion_date(StringUtils.isEmpty(formInfo.getChild_first_communion_date()) ? null : simpleDateFormat.parse(formInfo.getChild_first_communion_date()));
            child.setConfirmation_date(StringUtils.isEmpty(formInfo.getChild_confirmation_date()) ? null : simpleDateFormat.parse(formInfo.getChild_confirmation_date()));
            child.setGender(formInfo.getChild_gender());
            child.setAllergies(formInfo.getChild_allergies());
            child.setMedical_needs(formInfo.getChild_medical_needs());
            child.setHobbies(formInfo.getChild_hobbies());
            child.setPtrParent(newParent.getId());
            child.setCreatedDate(dateCreated);

        // Validate SR before we save!!!!!
//        if (!Utils.isSrKfc(child)) {
//            throw new IllegalArgumentException("Cannot sign up " + child.getFull_name() + ". Child age is only " + Utils.getAge(child) + " years old.");
//        }

            childRepository.save(child);

            Event event = eventRepository.findByName("jr_kids_day");
            eventController.signUpChild(child.getId(), event.getId(), new Date());

            redir.addFlashAttribute("confirmedKids", child.getFull_name());
            RedirectView redirectView = new RedirectView(EventController.CONTROLLER_URL + "/jr-kids-day#what-now", true);
            return redirectView;
        } catch(Exception e) {
            System.out.println("trying to save::: " + formInfo);
            throw new IllegalArgumentException(e.getMessage());
        }

//        model.addAttribute("child", child);
//        model.addAttribute("parent", newParent);

//        return CONTROLLER_PATH + "/showMessage";
//        return CONTROLLER_PATH + "/showMessage";
    }
}
