package de.dfki.step.web;

import de.dfki.step.dialog.Dialog;
import de.dfki.step.dialog.MyDialog20;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


import java.util.Map;

@RestController
public class ConfidenceController {
    private Dialog dialog;

    @Autowired
    private AppConfig appConfig;

   // @PostConstruct
    protected void init() {
        dialog = appConfig.getDialog();
    }

    /**
     * Create HTTP POST function for setting the confidence from external components.
     * Requires {'confidence':0.4}
     * @param id
     * @param body
     * @return
     */
    @PostMapping(value = "/confidence/{id}")
    public ResponseEntity setConfidence(@PathVariable("id") String id, @RequestBody Map<String, Object> body) {
        try {
            Double confidence = (Double) body.get("confidence");
            dialog.retrieveComponent(MyDialog20.ConfidenceAdapationComponent.class).setConfidence(id, confidence);
            return ResponseEntity.status(HttpStatus.OK).body("ok");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }
}
