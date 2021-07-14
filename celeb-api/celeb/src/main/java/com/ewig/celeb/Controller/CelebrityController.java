package com.ewig.celeb.Controller;

import com.ewig.celeb.DTO.CelebrityDTO;
import com.ewig.celeb.DTO.ChangepwdDTO;
import com.ewig.celeb.Document.Celebrity;
import com.ewig.celeb.Service.CelebrityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
public class CelebrityController {

    @Autowired
    private CelebrityService celebrityService;

    @RequestMapping(value = "/celebrity/registration", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public String addCelebrity(@RequestPart("multipartFile") MultipartFile multipartFile, @RequestPart CelebrityDTO celebrityDTO) throws IOException {
        return celebrityService.registerCelebrity(celebrityDTO, multipartFile);
    }

    @PostMapping("/email")
    public String sendEmail(@RequestBody CelebrityDTO celebrityDTO){
        return celebrityService.sentEmail(celebrityDTO);
    }

    @RequestMapping(value = "/confirm-account", method = {RequestMethod.GET, RequestMethod.POST})
    public String verify(@RequestParam("token") String confirmationToken){
        return celebrityService.verifyCelebrity(confirmationToken);

    }

    @PostMapping("/loginCelebrity")
    public List<Celebrity> login(){
        return celebrityService.login();
    }

    @GetMapping("/celebrity/logout")
    public ResponseEntity<String> logout() {
        //return ResponseEntity.ok("<h1> you are logged out from celeb app</h1>");
        SecurityContextHolder.getContext().setAuthentication(null);
        return ResponseEntity.ok("<h1> you are logged out from celeb app</h1>");
    }

    @PutMapping("/celebrity/changepwd")
    public String chagepwd(@RequestBody ChangepwdDTO changepwdDTO) {
        return celebrityService.changePassword(changepwdDTO);
    }
}
