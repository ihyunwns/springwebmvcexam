package com.hyunwns.demoweb.controller;

import com.hyunwns.demoweb.domain.Member;
import com.hyunwns.demoweb.domain.Post;
import com.hyunwns.demoweb.dto.PostForm;
import com.hyunwns.demoweb.repository.PostSearch;
import com.hyunwns.demoweb.service.MemberService;
import com.hyunwns.demoweb.service.NoticeBoardService;
import com.hyunwns.demoweb.util.SecurityUtils;
import jakarta.servlet.ServletContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class PostController {

    private static final String uploadDIR = "C:\\Users\\ihyun\\Desktop\\springmvcwebexam\\src\\main\\resources\\thumbnail\\";

    private final SecurityUtils securityUtils;
    private final NoticeBoardService noticeBoardService;
    private final MemberService memberService;

    @GetMapping("/display/{filename}")
    public ResponseEntity<Resource> display(@PathVariable("filename") String filename) {
        try {
            // 파일 경로 설정
            Path file = Paths.get(uploadDIR + filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                // 이미지 파일을 브라우저에 보여줌
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("post")
    public String postForm(Model model) {
        securityUtils.addAttributeUserInfo(model);
        model.addAttribute("postRequest", new PostForm());

        return "board/postForm";
    }

    @PostMapping("post")
    public String post(@ModelAttribute("postRequest") PostForm postRequest, Model model, BindingResult bindingResult){
        try {
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

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String id = authentication.getName();
            Member findMember = memberService.findMember(id);
            Post post = new Post(findMember, title, content);

            String filename = UUID.randomUUID() + "-" + post.getTitle();

            String name = postRequest.getImgFile().getOriginalFilename();
            String extension = name.substring(name.lastIndexOf(".") + 1); // 확장자 추출

            post.setThumbnailName(filename + "." + extension);
            post.setThumbnailURL(uploadThumbnail(postRequest.getImgFile(), filename, extension));

            noticeBoardService.post(post);

            return "redirect:/main";
        } catch (IOException e) {
            bindingResult.addError(new FieldError("postRequest", "imgFile", "이미지 업로드 중 오류가 발생했습니다."));
            return "board/postForm";
        }
    }

    //MIME 타입을 확인
    public boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (contentType.startsWith("image/"));
    }

    public String uploadThumbnail(MultipartFile file, String filename, String extension) throws IOException {
        BufferedImage resizedImage = resizeImage(file, 350, 150);

        File outputFile = new File(uploadDIR + filename + "." + extension);
        ImageIO.write(resizedImage, extension, outputFile);

        return outputFile.getPath();
    }

    public BufferedImage resizeImage(MultipartFile file, int targetWidth, int targetHeight) throws IOException {
        // MultipartFile 을 InputStream 으로 변환하여 이미지 읽기
        InputStream inputStream = file.getInputStream();
        BufferedImage originalImage = ImageIO.read(inputStream);

        // 새로운 크기의 BufferedImage 생성
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH), 0, 0, null);
        g2d.dispose();

        return resizedImage;
    }

}
