package com.photoshare.repository;

import com.photoshare.model.Tag;
import com.photoshare.model.User;
import com.photoshare.model.Picture;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findByUser(User user);
    List<Tag> findByPicture(Picture picture);
}
