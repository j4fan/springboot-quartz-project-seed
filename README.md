![Hex.pm](https://img.shields.io/hexpm/l/plug.svg)![TeamCity CodeBetter](https://img.shields.io/teamcity/codebetter/bt428.svg)

## 1.场景
当你需要在工作中实现定时任务，例如定时报表，每天凌晨的定时执行的代码，这些任务需要统一调度，你希望方便的查看某个任务下次的执行时间，灵活的添加任务，暂停任务，修改任务的定时时间...等等，本项目或许可以帮助你。

## 2.技术栈
* springboot
* quartz
* adminLTE(bootstrap)
* mysql
* ...

## 3.后端api
#### 后端api遵守**restful-api**设计原则
![image.png](https://upload-images.jianshu.io/upload_images/5834071-73de27e91ab57d10.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
项目启动后可访问swagger2链接进行api测试
<http://localhost:8081/quartz-project/swagger-ui.html/>  

## 4.前端页面
#### 前端页面套用adminlte框架
项目启动后可访问后台地址针对任务进行操作
<http://localhost:8081/quartz-project/starter.html>

![image.png](https://upload-images.jianshu.io/upload_images/5834071-9ef932ae8350b592.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

## 5.操作指南
1.**在项目中添加任务**

```
@Component
@Log4j2
public class TestJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("TestJob is executing...");
    }
}
```

2.**重新发布代码**

BeanInitialConfig这个类用于获取ApplicationContext，并将类名注册在数据库中

```
@Component
public class BeanInitialConfig implements ApplicationContextAware {

    @Autowired
    ClassMapper mapper;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        String[] beans = applicationContext.getBeanNamesForType(Job.class);

        mapper.truncateNames();
        List<ClassFullNames> namesList = new ArrayList<>();
        Arrays.asList(beans).forEach(bean ->
                {
                    Class className = applicationContext.getBean(bean).getClass();
                    namesList.add(new ClassFullNames(className.getName().split("\\$")[0], className.getSimpleName().split("\\$")[0]));
                }
        );
        //remove proxy class
        namesList.remove(new ClassFullNames("com.sun.proxy.", "$Proxy88"));
        namesList.remove(new ClassFullNames("com.sun.proxy.", ""));
        if (namesList.size() > 0) {
            mapper.insertClassNames(namesList);
        }

    }
}
```

打开数据库，qrtz_class_name已经存入了所有的Job子类
![image.png](https://upload-images.jianshu.io/upload_images/5834071-86ff576dc3ca6ffc.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

Job的Group归属也需要在数据库中初始化一下，用于前端方便的选择，下图是一个简单的示例
![image.png](https://upload-images.jianshu.io/upload_images/5834071-8403e7ca66d05efb.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

3.**打开后台，点击创建任务**
![image.png](https://upload-images.jianshu.io/upload_images/5834071-afd0a3d4a7a85834.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

点击确认创建完毕，对应的定时表达式：每分钟执行一次testJob

![image.png](https://upload-images.jianshu.io/upload_images/5834071-65a82183f656e443.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

创建完成后，可以在Java后台看到对应的log输出，说明对应的任务已经执行

![image.png](https://upload-images.jianshu.io/upload_images/5834071-d9985fd921e16638.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

除此之外，可以进行其他操作，例如立即执行，暂停，回复，删除...

![image.png](https://upload-images.jianshu.io/upload_images/5834071-b76beaef2c79e125.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

创建任务时，会有一些任务需要带参数，例如一个任务里有多个方法，通过传入参数来决定执行哪个方法
这时候我们需要在创建任务时填写param字段，这里我约定格式为json，方便后台进行解析，例如我们传入了

```
{"testStr":"123"}
```
在代码中只用如下处理即可轻松获得传入参数

```
@Component
@Log4j2
public class TestJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap map = context.getMergedJobDataMap();
        String inputParam = map.get("testStr").toString();
        log.info("inputParam:"+inputParam);
        log.info("TestJob is executing...");
    }
}
```
## 6.特性
* springboot,quartz完美集成，摒弃了properties文件配置的方式，用spring管理quartz的生命周期
* 拆箱即用，只需简单配置，初始化数据库，写好Job类，即可轻松实现定时任务调度

## 7.说明
由于本项目设定的场景为定时任务，因此默认创建的Job自带的trigger为cronTrigger,对应的misFire的策略为withMisfireHandlingInstructionDoNothing，这些都是根据我的场景定制的，同学们可以自行修改。quartz支持其他形式的trigger,例如只执行一次的，间隔一段时间执行的...有兴趣的同学可以研究后扩展我的api,
创建Job的代码就不贴出来了，quartz官方地址在此，文档很详细：

<http://www.quartz-scheduler.org/>

贴出定时表达式这部分，希望有帮助

>Cron-Expressions are used to configure instances of CronTrigger. Cron-Expressions are strings that are actually made up of seven sub-expressions, that describe individual details of the schedule. These sub-expression are separated with white-space, and represent: 
* Seconds
* Minutes
* Hours
* Day-of-Month
* Month
* Day-of-Week
* Year (optional field)



## 8.部署 
支持多环境部署,以正式环境为例:
```
mvn clean package 
java -jar target/springboot-quartz-project-seed.jar --spring.profiles.active=prod
```

