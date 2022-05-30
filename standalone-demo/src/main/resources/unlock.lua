-- 比较线程标志与锁汇总的标志是否一致
if (redis.call('get', KEYS[1]) == ARGV[1]) then
    -- 一致，则释放锁
    return redis.call('del', KEYS[1])
end
-- 不一致，则直接返回
return 0