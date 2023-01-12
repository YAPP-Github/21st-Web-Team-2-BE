package com.yapp.web2.domain.topic.repository.option

import com.yapp.web2.domain.topic.model.option.VoteOption
import org.springframework.data.jpa.repository.JpaRepository

interface VoteOptionRepository : JpaRepository<VoteOption, Long>
