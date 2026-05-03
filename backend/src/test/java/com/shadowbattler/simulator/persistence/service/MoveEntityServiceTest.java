package com.shadowbattler.simulator.persistence.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import com.shadowbattler.simulator.model.Move;
import com.shadowbattler.simulator.persistence.entity.MoveEntity;
import com.shadowbattler.simulator.service.MovesDataService;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Do not replace the datasource
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:file:./build/h2-db/testdb;DB_CLOSE_DELAY=-1", // Use a file-based H2 database for tests
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Transactional // Ensures each test runs in an isolated transaction and rolls back changes
public class MoveEntityServiceTest {

    @Autowired
    private MoveEntityService moveEntityService;

    @Autowired
    private MovesDataService movesDataService;

    private Move testMove1;
    private Move testMove2;

    @BeforeEach
    void setUp() {
        // Ensure some moves exist in the data service for testing
        testMove1 = movesDataService.getMoveById("CONFUSION");
        testMove2 = movesDataService.getMoveById("PSYCHIC_FANGS");

        // Save these moves to the database for testing purposes
        moveEntityService.saveMove(testMove1);
        moveEntityService.saveMove(testMove2);
    }

    @Test
    void testGetAllMoveEntities() {
        List<MoveEntity> allMoves = moveEntityService.getAllMoveEntities();
        assertNotNull(allMoves);
        // We expect at least the two moves saved in setUp
        assertTrue(allMoves.size() >= 2);
        assertTrue(allMoves.stream().anyMatch(m -> m.getMoveId().equals(testMove1.moveId())));
        assertTrue(allMoves.stream().anyMatch(m -> m.getMoveId().equals(testMove2.moveId())));
    }

    @Test
    void testGetMoveEntityById() {
        Optional<MoveEntity> foundMove = moveEntityService.getMoveEntityById(testMove1.moveId());
        assertTrue(foundMove.isPresent());
        assertEquals(testMove1.moveId(), foundMove.get().getMoveId());
        assertEquals(testMove1.power(), foundMove.get().getPower());
    }

    @Test
    void testGetReferenceById() {
        MoveEntity referenceMove = moveEntityService.getReferenceById(testMove1.moveId());
        assertNotNull(referenceMove);
        assertEquals(testMove1.moveId(), referenceMove.getMoveId());
    }

    @Test
    void testSaveMove() {
        // Test saving a new move
        Move newMove = movesDataService.getMoveById("WATERFALL"); // Assuming WATERFALL is not testMove1 or testMove2
        assertFalse(moveEntityService.getMoveEntityById(newMove.moveId()).isPresent()); // Ensure it doesn't exist yet

        MoveEntity savedEntity = moveEntityService.saveMove(newMove);
        assertNotNull(savedEntity);
        assertEquals(newMove.moveId(), savedEntity.getMoveId());

        Optional<MoveEntity> fetchedNewEntity = moveEntityService.getMoveEntityById(newMove.moveId());
        assertTrue(fetchedNewEntity.isPresent());
        assertEquals(newMove.moveId(), fetchedNewEntity.get().getMoveId());

        // Test updating an existing move
        // Create a modified version of testMove1 (assuming Move is an immutable record/data class)
        Move modifiedMove = new Move(
            testMove1.moveId(),
            "Modified " + testMove1.name(), // Change a property to simulate an update
            testMove1.abbreviation(),
            testMove1.type(),
            testMove1.power() + 10, // Also change power
            testMove1.energy(),
            testMove1.energyGain(),
            testMove1.buffsSelf(),
            testMove1.buffsOpponent(),
            testMove1.buffApplyChance(),
            testMove1.archetype(),
            testMove1.turns()
        );

        MoveEntity updatedEntity = moveEntityService.saveMove(modifiedMove);
        assertNotNull(updatedEntity);
        assertEquals(modifiedMove.moveId(), updatedEntity.getMoveId());
        assertEquals(modifiedMove.power(), updatedEntity.getPower());

        Optional<MoveEntity> fetchedUpdatedEntity = moveEntityService.getMoveEntityById(modifiedMove.moveId());
        assertTrue(fetchedUpdatedEntity.isPresent());
        assertEquals(modifiedMove.moveId(), fetchedUpdatedEntity.get().getMoveId());
        assertEquals(modifiedMove.power(), fetchedUpdatedEntity.get().getPower());
    }
}
