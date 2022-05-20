package mao.spring_boot_redis_hmdp.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Project name(项目名称)：spring_boot_redis_hmdp_message_queue_realizes_asynchronous_spike
 * Package(包名): mao.spring_boot_redis_hmdp.config
 * Class(类名): RabbitMQConfig
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2022/5/17
 * Time(创建时间)： 22:23
 * Version(版本): 1.0
 * Description(描述)： RabbitMQ基本配置，暂时不考虑消息丢失问题
 */

@Configuration
public class RabbitMQConfig
{
    /**
     * 交换机名称
     */
    public static final String EXCHANGE_NAME = "hmdp_exchange";
    /**
     * 队列名称
     */
    public static final String QUEUE_NAME = "hmdp_queue";
    /**
     * routingKey
     */
    public static final String ROUTING_KEY = "VoucherOrder";

    /**
     * 声明直接交换机
     *
     * @return DirectExchange
     */
    @Bean
    public DirectExchange directExchange()
    {
        return new DirectExchange(EXCHANGE_NAME, false, false, null);
    }

    /**
     * 声明队列
     *
     * @return Queue
     */
    @Bean
    public Queue queue()
    {
        return new Queue(QUEUE_NAME, false, false, false, null);
    }

    /**
     * 绑定
     *
     * @return Binding
     */
    @Bean
    public Binding exchange_binding_queue()
    {
        return BindingBuilder.bind(queue()).to(directExchange()).with(ROUTING_KEY);
    }

    /**
     * 使用json传递消息
     *
     * @return Jackson2JsonMessageConverter
     */
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter()
    {
        return new Jackson2JsonMessageConverter();
    }
}
