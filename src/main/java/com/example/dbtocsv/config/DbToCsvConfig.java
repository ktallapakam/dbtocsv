package com.example.dbtocsv.config;


import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.example.dbtocsv.processor.DbToCsvProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.example.dbtocsv.entity.Student;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.PlatformTransactionManager;



@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class DbToCsvConfig
{
    private final DbToCsvProcessor dbToCsvProcessor;
    private final DataSource dataSource;

    @Bean
    public Job dbtocsv(JobRepository jobRepository, PlatformTransactionManager transactionManager)
    {
        log.info("dbtocsv");
        return new JobBuilder("dbtocsv", jobRepository)
                .incrementer(new RunIdIncrementer())
                .flow(dbtocsvStep(jobRepository, transactionManager))
                .end()
                .build();
    }

    private Step dbtocsvStep(JobRepository jobRepository, PlatformTransactionManager transactionManager)
    {
        log.info("dbtocsvStep");
        return new StepBuilder("dbtocsvStep", jobRepository)
                .<Student, Student>chunk(10, transactionManager)
                .reader(dbReader())
                .processor(dbprocessor())
                .writer(csvWriter())
                .build();
    }

    @Bean
    public ItemReader<? extends Student> dbReader()
    {
        log.info("dbReader");
        log.info("D***atasource: {}", dataSource);

        JdbcCursorItemReader<Student> itemReader = new JdbcCursorItemReader<Student>();
        itemReader.setSql("select * from student");
        itemReader.setDataSource(dataSource);
        itemReader.setRowMapper(new RowMapper<Student>() {
            @Override
            public Student mapRow(ResultSet rs, int rowNum) throws SQLException {
                log.info("mapRow: "+rs.getRow());
                Student student = new Student();
                student.setId(rs.getInt("id"));
                student.setFirstName(rs.getString("first_name"));
                student.setLastName(rs.getString("last_name"));
                student.setAge(rs.getInt("age"));
                student.setBirthDate(rs.getString("birth_date"));
                student.setGender(rs.getString("gender"));
                student.setEmail(rs.getString("email"));
                student.setPhone(rs.getString("phone"));
                return student;
            }
        });
        return itemReader;
    }

    @Bean
    public ItemProcessor<? super Student,? extends Student> dbprocessor()
    {
        log.info("dbprocessor");
        return dbToCsvProcessor;
    }

    @Bean
    public ItemWriter<? super Student> csvWriter()
    {
        log.info("csvWriter");
        FlatFileItemWriter<Student> itemWriter = new FlatFileItemWriter<Student>();
        itemWriter.setResource(new FileSystemResource("C:\\IdeaProjects\\DbToCsv\\DbToCsv\\src\\main\\resources\\dbtocsv.csv"));

        DelimitedLineAggregator<Student> aggregator = new DelimitedLineAggregator<>();
        BeanWrapperFieldExtractor<Student> extractor = new BeanWrapperFieldExtractor<>();
        extractor.setNames(new String[]{"id","firstName", "lastName", "age", "birthDate", "gender", "email", "phone"});
        aggregator.setFieldExtractor(extractor);
        itemWriter.setLineAggregator(aggregator);

        return itemWriter;
    }
}