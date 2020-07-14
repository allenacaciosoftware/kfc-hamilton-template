package com.example.demo.kfc.event;

import com.example.demo.Utils;
import com.example.demo.kfc.FormInfo;
import com.example.demo.kfc.MyController;
import com.example.demo.kfc.PdfController;
import com.example.demo.repository.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.demo.Utils.KFC_SR_CAMP_ALLOWED_PARTICIPANTS;
import static com.example.demo.Utils.SR_KFC_AGE_THRESHOLD;

@Controller
@RequestMapping(EventController.CONTROLLER_URL)
public class EventController {
    private static final String CONTROLLER_PATH = "kfc/event";
    public static final String CONTROLLER_URL = "/" + CONTROLLER_PATH;

    @Autowired
    ResourceLoader resourceLoader;
    @Autowired
    ParentRepository parentRepository;
    @Autowired
    ChildRepository childRepository;
    @Autowired
    EventRepository eventRepository;
    @Autowired
    EventChildRepository eventChildRepository;

    @GetMapping("/sr-camp")
    public String srCamp(Model model) {
        Event event = eventRepository.findByName("kfc_sr_camp_2020");
        List<EventChild> childParticipants = eventChildRepository.findByPtrEvent(event.getId());

        int slotsAvailable = KFC_SR_CAMP_ALLOWED_PARTICIPANTS - childParticipants.size();
        model.addAttribute("slotsAvailable", slotsAvailable);
        return CONTROLLER_PATH + "/sr-camp";
    }

    @GetMapping("/sr-camp-registration")
    public String srCampRegistration() {
        return CONTROLLER_PATH + "/sr-camp-registration";
    }

    @PostMapping({"/sr-camp-registration"})
    public String srCampEmailVerification(FormInfo formInfo, Model model) {
        String[] parentEmails = formInfo.getParent_email().trim().split(",");
        Parent parent = null;
        for (String parentEmail : parentEmails) {
            parent = parentRepository.findByEmailContaining(parentEmail);
            if (parent != null && parent.getId() > 0) {
                break;
            }
        }

        if (parent == null) {
            model.addAttribute("parentEmail", formInfo.getParent_email());
            return "kfc/user/add-user";
        }

        Event event = eventRepository.findByName("kfc_sr_camp_2020");

        model.addAttribute("eventId", event.getId());
        model.addAttribute("srKids", getSrKids(parent));
        model.addAttribute("emailVerified", true);
        return CONTROLLER_PATH + "/sr-camp-registration";
    }

    private List<Child> getSrKids(Parent parent) {
        return childRepository.findChildrenByParentId(parent.getId())
                .stream()
                .filter(child -> Utils.getDiffYears(child.getBirth_date(), new Date()) >= SR_KFC_AGE_THRESHOLD)
                .collect(Collectors.toList());
    }

    private List<Child> getKids(Parent parent) {
        return childRepository.findChildrenByParentId(parent.getId())
                .stream()
//                .filter(child -> Utils.getDiffYears(child.getBirth_date(), new Date()) >= SR_KFC_AGE_THRESHOLD)
                .collect(Collectors.toList());
    }

    public void signUpChild(long childId, long eventId, Date dateCreated) {
        if (childRepository.findById(childId).isPresent()) {
            EventChild eventChild = eventChildRepository.findByPtrChildAndPtrEvent(childId, eventId);
            if (eventChild == null) {
                eventChild = new EventChild();
            }
            eventChild.setPtrEvent(eventId);
            eventChild.setPtrChild(childId);
            eventChild.setCreated_date(dateCreated);
            eventChildRepository.save(eventChild);

        }

    }

    @PostMapping("/sr-camp-registration-confirmation")
    public RedirectView srCampConfirmation(EventInfo eventInfo, RedirectAttributes redir) {
        Event event = eventRepository.findByName("kfc_sr_camp_2020");
        List<EventChild> childParticipants = eventChildRepository.findByPtrEvent(event.getId());
        if (childParticipants.size() >= KFC_SR_CAMP_ALLOWED_PARTICIPANTS) {
            throw new IllegalStateException("Cannot sign up child/ren. Maximum number of child participants (" + KFC_SR_CAMP_ALLOWED_PARTICIPANTS + ") is reached.");
        }
        Date dateCreated = new Date();
        List<Child> confirmedKids = new LinkedList<>();
        eventInfo.getChildIds().forEach(childId -> {
            signUpChild(childId, eventInfo.getEventId(), dateCreated);
            confirmedKids.add(childRepository.findById(childId).get());
        });

        redir.addFlashAttribute("confirmedKids",
                StringUtils.join(confirmedKids.stream()
                .map(confirmedKid -> confirmedKid.getFull_name())
                .collect(Collectors.toList()), ", ")
        );
        RedirectView redirectView= new RedirectView(CONTROLLER_URL + "/sr-camp#what-now",true);
        return redirectView;
    }

    // JR kids day
    @GetMapping("/jr-kids-day")
    public String jrKidsDay(Model model) {
//        Event event = eventRepository.findByName("kfc_sr_camp_2020");
//        List<EventChild> childParticipants = eventChildRepository.findByPtrEvent(event.getId());
//
//        int slotsAvailable = KFC_SR_CAMP_ALLOWED_PARTICIPANTS - childParticipants.size();
//        model.addAttribute("slotsAvailable", slotsAvailable);
        return CONTROLLER_PATH + "/jr-kids-day";
    }

    @GetMapping("/jr-kids-day-registration")
    public String jrKidsDayRegistration() {
        return CONTROLLER_PATH + "/jr-kids-day-registration";
    }

    @PostMapping({"/jr-kids-day-registration"})
    public String jrKidsDayEmailVerification(FormInfo formInfo, Model model) {
        String[] parentEmails = formInfo.getParent_email().trim().split(",");
        Parent parent = null;
        for (String parentEmail : parentEmails) {
            parent = parentRepository.findByEmailContaining(parentEmail);
            if (parent != null && parent.getId() > 0) {
                break;
            }
        }

        if (parent == null) {
            model.addAttribute("parentEmail", formInfo.getParent_email());
            return "kfc/user/add-user";
        }

        Event event = eventRepository.findByName("jr_kids_day");

        model.addAttribute("eventId", event.getId());
        model.addAttribute("srKids", getKids(parent));
        model.addAttribute("emailVerified", true);
        return CONTROLLER_PATH + "/jr-kids-day-registration";
    }



    @PostMapping("/jr-kids-day-registration-confirmation")
    public RedirectView jrKidsDayConfirmation(EventInfo eventInfo, RedirectAttributes redir) {
        Event event = eventRepository.findByName("jr_kids_day");
        List<EventChild> childParticipants = eventChildRepository.findByPtrEvent(event.getId());
//        if (childParticipants.size() >= KFC_SR_CAMP_ALLOWED_PARTICIPANTS) {
//            throw new IllegalStateException("Cannot sign up child/ren. Maximum number of child participants (" + KFC_SR_CAMP_ALLOWED_PARTICIPANTS + ") is reached.");
//        }
        Date dateCreated = new Date();
        List<Child> confirmedKids = new LinkedList<>();
        eventInfo.getChildIds().forEach(childId -> {
            signUpChild(childId, eventInfo.getEventId(), dateCreated);
            confirmedKids.add(childRepository.findById(childId).get());
        });

        redir.addFlashAttribute("confirmedKids",
                StringUtils.join(confirmedKids.stream()
                        .map(confirmedKid -> confirmedKid.getFull_name())
                        .collect(Collectors.toList()), ", ")
        );
        RedirectView redirectView= new RedirectView(CONTROLLER_URL + "/jr-kids-day#what-now",true);
        return redirectView;
    }
}
