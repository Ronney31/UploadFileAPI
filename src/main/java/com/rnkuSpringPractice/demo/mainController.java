package com.rnkuSpringPractice.demo;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.rnkuSpringPractice.demo.storage.FileSystemStorageService;

@Controller
public class mainController {
	
	private final FileSystemStorageService storageService = new FileSystemStorageService();

	@GetMapping("")
	public String controller() {
		return "redirect:/upload";
	}
	
	/**
	 * 
	 * @param file
	 * @param redirectAttributes
	 * @return
	 */
	@PostMapping("/upload/up")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {

        storageService.store(file);
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/upload";
    }

	/**
	 * direct callable from post
	 * if the file is already present in directory, it will return failure message
	 * @param file
	 * @return
	 */
	@ResponseBody
	@PostMapping("/uploadFile")
    public Map<String,String> handleFileUpload(@RequestParam("file") MultipartFile file) {
        String message = storageService.storeFile(file);
        HashMap<String, String> ret = new HashMap<String, String>();
        ret.put("Message", message);
        return ret;
    }
}
