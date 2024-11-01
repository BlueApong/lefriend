-- keys
-- 队伍集合key
local teamKey = KEYS[1]

-- argv
-- 队伍最大成员数
local maxTeamUserNum = ARGV[1]
-- 目标用户
local userId = ARGV[2]

-- 队伍是否满员 SCARD t1 >= maxTeamUserNum
if(redis.call('SCARD', teamKey) >= tonumber(maxTeamUserNum)) then
    return 1
end

-- 该用户是否已加入队伍
if(redis.call('SISMEMBER', teamKey, userId) == 1) then
    return 2
end

-- 添加用户到队伍
redis.call('SADD', teamKey, userId)

-- 允许加入队伍
return 0
