package com.example.dbtocsv.processor;

import com.example.dbtocsv.entity.Student;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class DbToCsvProcessor implements ItemProcessor<Student, Student>
{
    
    @Override
    public Student process(Student item) throws Exception
    {
        return item;
    }
}
