package com.photoshare.service;

import ai.djl.Application;
import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import com.photoshare.model.Picture;
import com.photoshare.model.Tag;
import com.photoshare.model.User;
import com.photoshare.repository.TagRepository;
import com.photoshare.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.util.List;

@Service
public class FaceRecognitionService {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UserRepository userRepository;

    public void recognizeFaces(Picture picture) {
        try {
            Image img = ImageFactory.getInstance().fromFile(Paths.get("upload-dir").resolve(picture.getUrl().substring(9)));

            Criteria<Image, DetectedObjects> criteria = Criteria.builder()
                    .optApplication(Application.CV.OBJECT_DETECTION)
                    .setTypes(Image.class, DetectedObjects.class)
                    .optFilter("face", "")
                    .build();

            try (ZooModel<Image, DetectedObjects> model = criteria.loadModel();
                 Predictor<Image, DetectedObjects> predictor = model.newPredictor()) {

                DetectedObjects detectedObjects = predictor.predict(img);
                List<User> allUsers = userRepository.findAll();

                // Default tag
                Tag defaultTag = new Tag();
                defaultTag.setPicture(picture);
                defaultTag.setUser(picture.getUploader());
                tagRepository.save(defaultTag);

                if (detectedObjects != null) {
                    detectedObjects.items().forEach(result -> {
                        // In a real application, we would compare the detected face with user profile pictures
                        // For simplicity, we'll just tag a random user.
                        if (!allUsers.isEmpty()) {
                            User randomUser = allUsers.get((int) (Math.random() * allUsers.size()));
                            if (!randomUser.equals(picture.getUploader())) {
                                Tag tag = new Tag();
                                tag.setPicture(picture);
                                tag.setUser(randomUser);
                                tagRepository.save(tag);
                            }
                        }
                    });
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to recognize faces", e);
        }
    }
}
