package com.hyunwns.demoweb.post.controller;

import com.hyunwns.demoweb.common.domain.Member;
import com.hyunwns.demoweb.post.domain.Post;
import com.hyunwns.demoweb.post.dto.PostForm;
import com.hyunwns.demoweb.common.service.MemberService;
import com.hyunwns.demoweb.post.service.NoticeBoardService;
import com.hyunwns.demoweb.common.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ContentDisposition;
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
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class PostController {

    private static final String uploadDIR = "C:\\Users\\user\\IdeaProjects\\springwebmvcexam-master\\src\\main\\resources\\thumbnail";

    private final SecurityUtils securityUtils;
    private final NoticeBoardService noticeBoardService;
    private final MemberService memberService;

    @GetMapping(value = "/details/{postId}/delete")
    public String delete(@PathVariable("postId") Long postId, Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Post post = noticeBoardService.findPost(postId);

        String id = auth.getName();
        Member member = memberService.findMember(id);
        if (member != post.getAuthor()) {
            return "redirect:/main";
        }

        noticeBoardService.delete(postId);
        return "redirect:/main";
    }

    @GetMapping(value = "/details/{postId}/edit")
    public String edit(@PathVariable("postId") Long postId, Model model) {
        Member member = securityUtils.addAttributeUserInfo(model);
        Post post = noticeBoardService.findPost(postId);

        if (member != post.getAuthor()) {
            return "redirect:/main";
        }

        PostForm postForm = new PostForm();
        postForm.setPostId(postId); postForm.setTitle(post.getTitle()); postForm.setContent(post.getContent());

        // 객체 바인딩
        model.addAttribute("postRequest", postForm);
        model.addAttribute("imageName", post.getThumbnailName());

        return "board/editForm";
    }

    @PostMapping("edit")
    public String edit(@ModelAttribute("postRequest") PostForm postRequest, Model model, BindingResult bindingResult){
        try {
            MultipartFile imgFile = postRequest.getImgFile();

            if (!imgFile.isEmpty() && !isImageFile(imgFile)) {
                model.addAttribute("title", postRequest.getTitle());
                model.addAttribute("content", postRequest.getContent());
                bindingResult.addError(new FieldError("postRequest", "imgFile", "잘못된 파일 형식입니다. 이미지 파일을 등록해주세요."));

                model.addAttribute("imageName", noticeBoardService.findPost(postRequest.getPostId()).getThumbnailName());
                return "board/editForm";
            }

            Post post = noticeBoardService.findPost(postRequest.getPostId());

            String title = postRequest.getTitle(); String content = postRequest.getContent();
            post.setTitle(title); post.setContent(content);

            if (!imgFile.isEmpty()) {
                String filename = UUID.randomUUID() + "-" + post.getTitle();
                String name = postRequest.getImgFile().getOriginalFilename();
                String extension = name.substring(name.lastIndexOf(".") + 1); // 확장자 추출

                post.setThumbnailName(filename + "." + extension);
                post.setThumbnailURL(uploadThumbnail(postRequest.getImgFile(), filename, extension));
            }

            noticeBoardService.post(post);

            return "redirect:/main";
        } catch (IOException e) {
            bindingResult.addError(new FieldError("postRequest", "imgFile", "이미지 업로드 중 오류가 발생했습니다."));
            return "board/postForm";
        }
    }

    @GetMapping(value = "/details/{postId}")
    public String details(@PathVariable("postId") Long postId, Model model) {
        Member member = securityUtils.addAttributeUserInfo(model);

        Post post = noticeBoardService.findPost(postId);
        if (member == post.getAuthor()) {
            model.addAttribute("POST_OWNER", "true");
        }

        model.addAttribute("content", post.getContent());
        model.addAttribute("title", post.getTitle());
        model.addAttribute("author", post.getAuthor());
        model.addAttribute("postId", postId);


        String date = post.getPublished().toString().split("\\.")[0];
        String replace = date.replace("T", " ");

        model.addAttribute("published", replace);

        return "board/boardForm";
    }

    @GetMapping("/display/{filename}")
    public ResponseEntity<Resource> display(@PathVariable("filename") String filename) {
        try {
            // 파일 경로 설정
            Path file = Paths.get(uploadDIR + filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                ContentDisposition contentDisposition = ContentDisposition.builder("inline")
                        .filename(resource.getFilename(), StandardCharsets.UTF_8)
                        .build();

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
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
