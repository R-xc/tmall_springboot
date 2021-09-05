package com.how2java.tmall.service;

import com.how2java.tmall.dao.UserDAO;
import com.how2java.tmall.pojo.User;
import com.how2java.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserDAO userDAO;

    public Page4Navigator<User> list(int start,int size,int navigatePages){

        Pageable pageable = new PageRequest(start,size,new Sort(Sort.Direction.ASC,"id"));

        Page page = userDAO.findAll(pageable);

        return  new Page4Navigator(page,navigatePages);

    }

    public boolean isExist(String name){
        User user = userDAO.findByName(name);

        return null!=user;//同名用户存在则返回ture
    }

    public void add(User user){
        userDAO.save(user);
    }

    public User login(String name,String passWord){

      return   userDAO.getByNameAndPassWord(name, passWord);
    }


    public User getByName(String userName) {
        return userDAO.getByName(userName);
    }
}
