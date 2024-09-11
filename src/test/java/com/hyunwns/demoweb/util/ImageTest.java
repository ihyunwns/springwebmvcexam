package com.hyunwns.demoweb.util;

import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ImageTest {

    private final int WIDTH = 320;
    private final int HEIGHT = 150;

    @Test
    public void 이미지_테스트() throws Exception{

        //Form으로 넘어온 이미지 파일을 받아서
        //given
        String imagePath = "C:\\Users\\ihyun\\Desktop\\springmvcwebexam\\src\\main\\resources\\thumbnail\\test.png";
        BufferedImage image = null;

        File imgFile = new File(imagePath);
        if(!imgFile.exists()){
            throw new RuntimeException("파일이 존재하지 않습니다." + imagePath);
        }

        image = ImageIO.read(imgFile);

        //then
        String fileName = imgFile.getName();
        try{
            BufferedImage bufferedImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
            bufferedImage.createGraphics().drawImage(image, 0, 0, WIDTH, HEIGHT, null);

            ImageIO.write(bufferedImage, "png", new File("C:\\Users\\ihyun\\Desktop\\springmvcwebexam\\src\\main\\resources\\output\\" + fileName));
            System.out.println("이미지가 성공적으로 저장되었습니다.");
        }catch (Exception e){
            System.out.println("에러 발생");
            e.printStackTrace();
        }
    }

    @Test
    public void maptest() throws Exception{
        //given
        Map<Integer, String> store = new HashMap<>();
        store.put(1, "테스트");

        //when

        store.put(1, "테스트2");

        //then
        System.out.println(store.get(1));

    }
}
