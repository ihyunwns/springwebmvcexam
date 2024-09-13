package com.hyunwns.demoweb.controller;

import com.hyunwns.demoweb.domain.Member;
import com.hyunwns.demoweb.domain.Post;
import com.hyunwns.demoweb.dto.PostForm;
import com.hyunwns.demoweb.service.MemberService;
import com.hyunwns.demoweb.service.NoticeBoardService;
import com.hyunwns.demoweb.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Controller
public class PostController {

    private final SecurityUtils securityUtils;
    private final NoticeBoardService noticeBoardService;
    private final MemberService memberService;

    @Autowired
    public PostController(SecurityUtils securityUtils, NoticeBoardService noticeBoardService, MemberService memberService) {
        this.securityUtils = securityUtils;
        this.noticeBoardService = noticeBoardService;
        this.memberService = memberService;
    }

    @GetMapping("post")
    public String postForm(Model model) {
        securityUtils.addAttributeUserInfo(model);
        model.addAttribute("postRequest", new PostForm());

        return "board/postForm";
    }

    @PostMapping("post")
    public String post(@ModelAttribute("postRequest") PostForm postRequest, Model model, BindingResult bindingResult) throws IOException {
        MultipartFile imgFile = postRequest.getImgFile();

        if (imgFile == null || imgFile.isEmpty()) {
            bindingResult.addError(new FieldError("postRequest", "imgFile", "썸네일 등록이 되지 않았습니다."));
        } else if (!isImageFile(postRequest.getImgFile())) {
            model.addAttribute("title", postRequest.getTitle());
            model.addAttribute("content", postRequest.getContent());
            bindingResult.addError(new FieldError("postRequest", "imgFile", "잘못된 파일 형식입니다. 이미지 파일을 등록해주세요."));
        }

        if(bindingResult.hasErrors()) {
            return "board/postForm";
        }

        String title = postRequest.getTitle();
        String content = postRequest.getContent();

        Post post = new Post(new Member("test", "asd", "asdsad", 24), title, content);

        noticeBoardService.post(post);

        resizeImage(postRequest.getImgFile(), 320, 150);

        return "redirect:/main";
    }

    //MIME 타입을 확인
    public boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (contentType.startsWith("image/"));
    }

    public void resizeImage(MultipartFile file, int targetWidth, int targetHeight) throws IOException {
        // MultipartFile 을 InputStream 으로 변환하여 이미지 읽기
        InputStream inputStream = file.getInputStream();
        BufferedImage originalImage = ImageIO.read(inputStream);
        String name = file.getOriginalFilename();

        String extension = name.substring(name.lastIndexOf(".") + 1); // 확장자 추출

        // 새로운 크기의 BufferedImage 생성
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH), 0, 0, null);
        g2d.dispose();

        File outputFile = new File("C:\\Users\\ihyun\\Desktop\\springmvcwebexam\\src\\main\\resources\\output\\" + name);
        ImageIO.write(resizedImage, extension, outputFile);

    }

}
