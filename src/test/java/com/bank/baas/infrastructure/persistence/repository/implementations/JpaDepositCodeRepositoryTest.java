package com.bank.baas.infrastructure.persistence.repository.implementations;

import com.bank.baas.domain.model.DepositCode;
import com.bank.baas.domain.model.User;
import com.bank.baas.infrastructure.persistence.entity.DepositCodeEntity;
import com.bank.baas.infrastructure.persistence.entity.UserEntity;
import com.bank.baas.infrastructure.persistence.mapper.DepositCodeMapper;
import com.bank.baas.infrastructure.persistence.mapper.UserMapper;
import com.bank.baas.infrastructure.persistence.repository.interfaces.SpringDataDepositCodeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JpaDepositCodeRepositoryTest {

    @Mock
    private SpringDataDepositCodeRepository depositCodeRepository;

    @Mock
    private DepositCodeMapper depositCodeMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private JpaDepositCodeRepository jpaDepositCodeRepository;

    private DepositCodeEntity depositCodeEntity;
    private DepositCode depositCode;
    private User generator;
    private UserEntity generatorEntity;
    private UUID depositCodeId;
    private String code;

    @BeforeEach
    void setUp() {
        depositCodeId = UUID.randomUUID();
        code = "DEP123";
        
        generator = new User("generator@example.com", "12345678900");
        generatorEntity = new UserEntity("generator@example.com", "12345678900");
        
        depositCode = new DepositCode(code, new BigDecimal("100.00"), generator);
        depositCodeEntity = new DepositCodeEntity();
        depositCodeEntity.setCode(code);
        depositCodeEntity.setGenerator(generatorEntity);
        
        // Set up reflection to set the ID
        try {
            java.lang.reflect.Field idField = DepositCodeEntity.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(depositCodeEntity, depositCodeId);
            
            idField = DepositCode.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(depositCode, depositCodeId);
            
            UUID generatorId = UUID.randomUUID();
            idField = UserEntity.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(generatorEntity, generatorId);
            
            idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(generator, generatorId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set IDs", e);
        }
    }

    @Test
    void save_ShouldReturnSavedDepositCode() {
        // Arrange
        when(depositCodeMapper.toEntity(depositCode)).thenReturn(depositCodeEntity);
        when(depositCodeRepository.save(depositCodeEntity)).thenReturn(depositCodeEntity);
        when(depositCodeMapper.toDomain(depositCodeEntity)).thenReturn(depositCode);

        // Act
        DepositCode result = jpaDepositCodeRepository.save(depositCode);

        // Assert
        assertEquals(depositCode, result);
        verify(depositCodeMapper).toEntity(depositCode);
        verify(depositCodeRepository).save(depositCodeEntity);
        verify(depositCodeMapper).toDomain(depositCodeEntity);
    }

    @Test
    void findById_ShouldReturnDepositCode_WhenDepositCodeExists() {
        // Arrange
        when(depositCodeRepository.findById(depositCodeId)).thenReturn(Optional.of(depositCodeEntity));
        when(depositCodeMapper.toDomain(depositCodeEntity)).thenReturn(depositCode);

        // Act
        Optional<DepositCode> result = jpaDepositCodeRepository.findById(depositCodeId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(depositCode, result.get());
        verify(depositCodeRepository).findById(depositCodeId);
        verify(depositCodeMapper).toDomain(depositCodeEntity);
    }

    @Test
    void findById_ShouldReturnEmpty_WhenDepositCodeDoesNotExist() {
        // Arrange
        when(depositCodeRepository.findById(depositCodeId)).thenReturn(Optional.empty());

        // Act
        Optional<DepositCode> result = jpaDepositCodeRepository.findById(depositCodeId);

        // Assert
        assertFalse(result.isPresent());
        verify(depositCodeRepository).findById(depositCodeId);
        verify(depositCodeMapper, never()).toDomain(any());
    }

    @Test
    void findByCode_ShouldReturnDepositCode_WhenDepositCodeExists() {
        // Arrange
        when(depositCodeRepository.findByCode(code)).thenReturn(Optional.of(depositCodeEntity));
        when(depositCodeMapper.toDomain(depositCodeEntity)).thenReturn(depositCode);

        // Act
        Optional<DepositCode> result = jpaDepositCodeRepository.findByCode(code);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(depositCode, result.get());
        verify(depositCodeRepository).findByCode(code);
        verify(depositCodeMapper).toDomain(depositCodeEntity);
    }

    @Test
    void findByCode_ShouldReturnEmpty_WhenDepositCodeDoesNotExist() {
        // Arrange
        when(depositCodeRepository.findByCode(code)).thenReturn(Optional.empty());

        // Act
        Optional<DepositCode> result = jpaDepositCodeRepository.findByCode(code);

        // Assert
        assertFalse(result.isPresent());
        verify(depositCodeRepository).findByCode(code);
        verify(depositCodeMapper, never()).toDomain(any());
    }

    @Test
    void findByGenerator_ShouldReturnDepositCodes_WhenDepositCodesExist() {
        // Arrange
        DepositCodeEntity depositCodeEntity2 = new DepositCodeEntity();
        depositCodeEntity2.setCode("DEP456");
        depositCodeEntity2.setGenerator(generatorEntity);
        
        DepositCode depositCode2 = new DepositCode("DEP456", new BigDecimal("200.00"), generator);
        
        List<DepositCodeEntity> depositCodeEntities = Arrays.asList(depositCodeEntity, depositCodeEntity2);
        List<DepositCode> depositCodes = Arrays.asList(depositCode, depositCode2);
        
        when(userMapper.toEntity(generator)).thenReturn(generatorEntity);
        when(depositCodeRepository.findByGenerator(generatorEntity)).thenReturn(depositCodeEntities);
        when(depositCodeMapper.toDomain(depositCodeEntity)).thenReturn(depositCode);
        when(depositCodeMapper.toDomain(depositCodeEntity2)).thenReturn(depositCode2);

        // Act
        List<DepositCode> result = jpaDepositCodeRepository.findByGenerator(generator);

        // Assert
        assertEquals(2, result.size());
        assertEquals(depositCodes, result);
        verify(userMapper).toEntity(generator);
        verify(depositCodeRepository).findByGenerator(generatorEntity);
        verify(depositCodeMapper, times(2)).toDomain(any(DepositCodeEntity.class));
    }

    @Test
    void findByGenerator_ShouldReturnEmptyList_WhenNoDepositCodesExist() {
        // Arrange
        when(userMapper.toEntity(generator)).thenReturn(generatorEntity);
        when(depositCodeRepository.findByGenerator(generatorEntity)).thenReturn(List.of());

        // Act
        List<DepositCode> result = jpaDepositCodeRepository.findByGenerator(generator);

        // Assert
        assertTrue(result.isEmpty());
        verify(userMapper).toEntity(generator);
        verify(depositCodeRepository).findByGenerator(generatorEntity);
        verify(depositCodeMapper, never()).toDomain(any());
    }

    @Test
    void findByGeneratorAndUsedFalse_ShouldReturnDepositCodes_WhenDepositCodesExist() {
        // Arrange
        DepositCodeEntity depositCodeEntity2 = new DepositCodeEntity();
        depositCodeEntity2.setCode("DEP456");
        depositCodeEntity2.setGenerator(generatorEntity);
        
        DepositCode depositCode2 = new DepositCode("DEP456", new BigDecimal("200.00"), generator);
        
        List<DepositCodeEntity> depositCodeEntities = Arrays.asList(depositCodeEntity, depositCodeEntity2);
        List<DepositCode> depositCodes = Arrays.asList(depositCode, depositCode2);
        
        when(userMapper.toEntity(generator)).thenReturn(generatorEntity);
        when(depositCodeRepository.findByGeneratorAndUsed(generatorEntity, false)).thenReturn(depositCodeEntities);
        when(depositCodeMapper.toDomain(depositCodeEntity)).thenReturn(depositCode);
        when(depositCodeMapper.toDomain(depositCodeEntity2)).thenReturn(depositCode2);

        // Act
        List<DepositCode> result = jpaDepositCodeRepository.findByGeneratorAndUsedFalse(generator);

        // Assert
        assertEquals(2, result.size());
        assertEquals(depositCodes, result);
        verify(userMapper).toEntity(generator);
        verify(depositCodeRepository).findByGeneratorAndUsed(generatorEntity, false);
        verify(depositCodeMapper, times(2)).toDomain(any(DepositCodeEntity.class));
    }

    @Test
    void findByGeneratorAndUsedFalse_ShouldReturnEmptyList_WhenNoDepositCodesExist() {
        // Arrange
        when(userMapper.toEntity(generator)).thenReturn(generatorEntity);
        when(depositCodeRepository.findByGeneratorAndUsed(generatorEntity, false)).thenReturn(List.of());

        // Act
        List<DepositCode> result = jpaDepositCodeRepository.findByGeneratorAndUsedFalse(generator);

        // Assert
        assertTrue(result.isEmpty());
        verify(userMapper).toEntity(generator);
        verify(depositCodeRepository).findByGeneratorAndUsed(generatorEntity, false);
        verify(depositCodeMapper, never()).toDomain(any());
    }

    @Test
    void findByGeneratorAndUsedTrue_ShouldReturnDepositCodes_WhenDepositCodesExist() {
        // Arrange
        DepositCodeEntity depositCodeEntity2 = new DepositCodeEntity();
        depositCodeEntity2.setCode("DEP456");
        depositCodeEntity2.setGenerator(generatorEntity);
        
        DepositCode depositCode2 = new DepositCode("DEP456", new BigDecimal("200.00"), generator);
        
        List<DepositCodeEntity> depositCodeEntities = Arrays.asList(depositCodeEntity, depositCodeEntity2);
        List<DepositCode> depositCodes = Arrays.asList(depositCode, depositCode2);
        
        when(userMapper.toEntity(generator)).thenReturn(generatorEntity);
        when(depositCodeRepository.findByGeneratorAndUsed(generatorEntity, true)).thenReturn(depositCodeEntities);
        when(depositCodeMapper.toDomain(depositCodeEntity)).thenReturn(depositCode);
        when(depositCodeMapper.toDomain(depositCodeEntity2)).thenReturn(depositCode2);

        // Act
        List<DepositCode> result = jpaDepositCodeRepository.findByGeneratorAndUsedTrue(generator);

        // Assert
        assertEquals(2, result.size());
        assertEquals(depositCodes, result);
        verify(userMapper).toEntity(generator);
        verify(depositCodeRepository).findByGeneratorAndUsed(generatorEntity, true);
        verify(depositCodeMapper, times(2)).toDomain(any(DepositCodeEntity.class));
    }

    @Test
    void findByGeneratorAndUsedTrue_ShouldReturnEmptyList_WhenNoDepositCodesExist() {
        // Arrange
        when(userMapper.toEntity(generator)).thenReturn(generatorEntity);
        when(depositCodeRepository.findByGeneratorAndUsed(generatorEntity, true)).thenReturn(List.of());

        // Act
        List<DepositCode> result = jpaDepositCodeRepository.findByGeneratorAndUsedTrue(generator);

        // Assert
        assertTrue(result.isEmpty());
        verify(userMapper).toEntity(generator);
        verify(depositCodeRepository).findByGeneratorAndUsed(generatorEntity, true);
        verify(depositCodeMapper, never()).toDomain(any());
    }

    @Test
    void findByUsedBy_ShouldReturnDepositCodes_WhenDepositCodesExist() {
        // Arrange
        User user = new User("user@example.com", "98765432100");
        UserEntity userEntity = new UserEntity("user@example.com", "98765432100");
        
        DepositCodeEntity depositCodeEntity2 = new DepositCodeEntity();
        depositCodeEntity2.setCode("DEP456");
        depositCodeEntity2.setGenerator(generatorEntity);
        depositCodeEntity2.setUsedBy(userEntity);
        
        DepositCode depositCode2 = new DepositCode("DEP456", new BigDecimal("200.00"), generator);
        
        List<DepositCodeEntity> depositCodeEntities = Arrays.asList(depositCodeEntity, depositCodeEntity2);
        List<DepositCode> depositCodes = Arrays.asList(depositCode, depositCode2);
        
        when(userMapper.toEntity(user)).thenReturn(userEntity);
        when(depositCodeRepository.findByUsedBy(userEntity)).thenReturn(depositCodeEntities);
        when(depositCodeMapper.toDomain(depositCodeEntity)).thenReturn(depositCode);
        when(depositCodeMapper.toDomain(depositCodeEntity2)).thenReturn(depositCode2);

        // Act
        List<DepositCode> result = jpaDepositCodeRepository.findByUsedBy(user);

        // Assert
        assertEquals(2, result.size());
        assertEquals(depositCodes, result);
        verify(userMapper).toEntity(user);
        verify(depositCodeRepository).findByUsedBy(userEntity);
        verify(depositCodeMapper, times(2)).toDomain(any(DepositCodeEntity.class));
    }

    @Test
    void findByUsedBy_ShouldReturnEmptyList_WhenNoDepositCodesExist() {
        // Arrange
        User user = new User("user@example.com", "98765432100");
        UserEntity userEntity = new UserEntity("user@example.com", "98765432100");
        
        when(userMapper.toEntity(user)).thenReturn(userEntity);
        when(depositCodeRepository.findByUsedBy(userEntity)).thenReturn(List.of());

        // Act
        List<DepositCode> result = jpaDepositCodeRepository.findByUsedBy(user);

        // Assert
        assertTrue(result.isEmpty());
        verify(userMapper).toEntity(user);
        verify(depositCodeRepository).findByUsedBy(userEntity);
        verify(depositCodeMapper, never()).toDomain(any());
    }

    @Test
    void existsByCode_ShouldReturnTrue_WhenDepositCodeExists() {
        // Arrange
        when(depositCodeRepository.existsByCode(code)).thenReturn(true);

        // Act
        boolean result = jpaDepositCodeRepository.existsByCode(code);

        // Assert
        assertTrue(result);
        verify(depositCodeRepository).existsByCode(code);
    }

    @Test
    void existsByCode_ShouldReturnFalse_WhenDepositCodeDoesNotExist() {
        // Arrange
        when(depositCodeRepository.existsByCode(code)).thenReturn(false);

        // Act
        boolean result = jpaDepositCodeRepository.existsByCode(code);

        // Assert
        assertFalse(result);
        verify(depositCodeRepository).existsByCode(code);
    }
}