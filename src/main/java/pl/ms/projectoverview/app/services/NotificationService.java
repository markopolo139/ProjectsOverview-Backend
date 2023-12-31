package pl.ms.projectoverview.app.services;

import com.google.firebase.messaging.FirebaseMessaging;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import pl.ms.projectoverview.app.exceptions.NotificationTokenAlreadyAddedException;
import pl.ms.projectoverview.app.exceptions.UserNotFoundException;
import pl.ms.projectoverview.app.persistence.entities.UserEntity;
import pl.ms.projectoverview.app.persistence.repositories.UserRepository;

@Service
public class NotificationService {

    private final Logger mLogger = LogManager.getLogger();
    private final UserRepository mUserRepository;

    public NotificationService(UserRepository userRepository) {
        mUserRepository = userRepository;
    }

    public void addToken(String token) throws UserNotFoundException, NotificationTokenAlreadyAddedException {
        UserEntity loggedInUser = AppUtils.getCurrentUser(mUserRepository);

        if (mUserRepository.existsByUserIdAndNotificationTokensContaining(loggedInUser.getUserId(), token)) {
            throw new NotificationTokenAlreadyAddedException();
        }

        loggedInUser.getNotificationTokens().add(token);

        mUserRepository.save(loggedInUser);
    }

    public void removeToken(String token) throws UserNotFoundException {
        UserEntity loggedInUser = AppUtils.getCurrentUser(mUserRepository);
        loggedInUser.getNotificationTokens().remove(token);

        mUserRepository.save(loggedInUser);
    }

    public void removeNotifications() throws UserNotFoundException {
        UserEntity loggedInUser = AppUtils.getCurrentUser(mUserRepository);
        loggedInUser.getNotificationTokens().clear();

        mUserRepository.save(loggedInUser);
    }
}
