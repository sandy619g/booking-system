package com.doodle.service;

import com.doodle.dto.request.CreateSlotRequest;
import com.doodle.dto.request.UpdateSlotRequest;
import com.doodle.dto.response.SlotResponse;
import com.doodle.entity.Slot;
import com.doodle.entity.SlotStatus;
import com.doodle.entity.User;
import com.doodle.repo.SlotRepository;
import com.doodle.repo.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SlotServiceTest {

    @Mock
    private SlotRepository slotRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SlotService slotService;

    private User user() {
        return User.builder()
                .id(1L)
                .name("John")
                .build();
    }

    private Slot slot(SlotStatus status) {
        return Slot.builder()
                .id(10L)
                .user(user())
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(1))
                .status(status)
                .build();
    }

    @Test
    void shouldCreateSlot() {

        CreateSlotRequest request = new CreateSlotRequest();
        request.setStartTime(LocalDateTime.now());
        request.setEndTime(LocalDateTime.now().plusHours(1));

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user()));

        when(slotRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        SlotResponse response = slotService.create(1L, request);

        assertNotNull(response);
        verify(slotRepository, times(1)).save(any());
    }

    @Test
    void shouldThrowWhenUserNotFound() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        CreateSlotRequest request = new CreateSlotRequest();
        request.setStartTime(LocalDateTime.now());
        request.setEndTime(LocalDateTime.now().plusHours(1));

        assertThrows(RuntimeException.class,
                () -> slotService.create(1L, request));
    }

    @Test
    void shouldReturnSlots() {

        when(slotRepository.findByUserId(1L))
                .thenReturn(List.of(slot(SlotStatus.FREE)));

        List<SlotResponse> result = slotService.getSlots(1L);

        assertEquals(1, result.size());
    }

    @Test
    void shouldUpdateSlot() {

        Slot existing = slot(SlotStatus.FREE);

        UpdateSlotRequest request = new UpdateSlotRequest();
        request.setStartTime(LocalDateTime.now());
        request.setEndTime(LocalDateTime.now().plusHours(2));

        when(slotRepository.findById(10L))
                .thenReturn(Optional.of(existing));

        when(slotRepository.existsByUserIdAndIdNotAndStartTimeLessThanAndEndTimeGreaterThan(
                any(), any(), any(), any()
        )).thenReturn(false);

        when(slotRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        SlotResponse response = slotService.update(10L, request);

        assertNotNull(response);
    }

    @Test
    void shouldThrowWhenUpdatingBusySlot() {

        Slot existing = slot(SlotStatus.BUSY);

        when(slotRepository.findById(10L))
                .thenReturn(Optional.of(existing));

        UpdateSlotRequest request = new UpdateSlotRequest();

        assertThrows(RuntimeException.class,
                () -> slotService.update(10L, request));
    }

    @Test
    void shouldUpdateStatus() {

        Slot existing = slot(SlotStatus.FREE);

        when(slotRepository.findById(10L))
                .thenReturn(Optional.of(existing));

        when(slotRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        SlotResponse response =
                slotService.updateStatus(10L, SlotStatus.BUSY);

        assertEquals(SlotStatus.BUSY, response.getStatus());
    }

    @Test
    void shouldDeleteSlot() {

        Slot existing = slot(SlotStatus.FREE);

        when(slotRepository.findById(10L))
                .thenReturn(Optional.of(existing));

        slotService.delete(10L);

        verify(slotRepository, times(1)).delete(existing);
    }

    @Test
    void shouldNotDeleteBusySlot() {

        Slot existing = slot(SlotStatus.BUSY);

        when(slotRepository.findById(10L))
                .thenReturn(Optional.of(existing));

        assertThrows(RuntimeException.class,
                () -> slotService.delete(10L));
    }

    @Test
    void shouldGetByStatus() {

        when(slotRepository.findByUserIdAndStatus(1L, SlotStatus.FREE))
                .thenReturn(List.of(slot(SlotStatus.FREE)));

        List<SlotResponse> result =
                slotService.getByStatus(1L, SlotStatus.FREE);

        assertEquals(1, result.size());
    }

    @Test
    void shouldGetBetweenDates() {

        when(slotRepository.findByUserIdAndStartTimeBetween(
                any(), any(), any()
        )).thenReturn(List.of(slot(SlotStatus.FREE)));

        List<SlotResponse> result =
                slotService.getBetween(
                        1L,
                        LocalDateTime.now(),
                        LocalDateTime.now().plusHours(1)
                );

        assertEquals(1, result.size());
    }
}