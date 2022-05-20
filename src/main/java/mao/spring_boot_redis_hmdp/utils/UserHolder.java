package mao.spring_boot_redis_hmdp.utils;


import mao.spring_boot_redis_hmdp.dto.UserDTO;

public class UserHolder
{
    private static final ThreadLocal<UserDTO> tl = new ThreadLocal<>();

    /**
     * 往ThreadLocal里保存信息
     *
     * @param user UserDTO对象
     */
    public static void saveUser(UserDTO user)
    {
        tl.set(user);
    }

    /**
     * 从ThreadLocal里取信息
     *
     * @return UserDTO对象
     */
    public static UserDTO getUser()
    {
        return tl.get();
    }

    /**
     * 从ThreadLocal里删除信息
     */
    public static void removeUser()
    {
        tl.remove();
    }
}
