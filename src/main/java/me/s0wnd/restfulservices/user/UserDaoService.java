package me.s0wnd.restfulservices.user;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


@Service
public class UserDaoService {
    private static List<User> users = new ArrayList<>();
    private static int usersCount = 3;

    static {
        users.add(new User(1, "name1", new Date(), "password1", "710101-1111111"));
        users.add(new User(2, "name2", new Date(), "password2", "810101-1111111"));
        users.add(new User(3, "name3", new Date(), "password3", "910101-2222222"));
    }

    public List<User> findAll() {
        return users;
    }

    public User save(User user) {
        if (user.getId() == null) {
            user.setId(++usersCount);
//            user.setJoinDate(new Date());
        }
        users.add(user);
        return user;
    }

    public User deleteById(int userId) {
        Iterator<User> iterator = users.iterator();

        while(iterator.hasNext()) {
            User user = iterator.next();
            if (user.getId() == userId) {
                iterator.remove();
                return user;
            }
        }
        return null;
    }

    public User findOne(int id) {
        for (User user : users) {
            if (user.getId() == id) {
                return user;
            }
        }
        return null;
    }
}
