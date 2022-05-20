-- 这里的 KEYS[1] 就是锁的 key，这里的 ARGV[1] 就是当前线程标识
-- 获取锁中的标识，判断是否与当前线程标识一致
if(redis.call('get', KEYS[1]) == ARGV[1]) then
    -- 一致，则删除锁
    return redis.call('del', KEYS[1])
end
-- 不一致，则直接返回
return 0