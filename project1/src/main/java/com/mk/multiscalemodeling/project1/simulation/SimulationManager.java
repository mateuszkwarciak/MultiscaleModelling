package com.mk.multiscalemodeling.project1.simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import com.mk.multiscalemodeling.project1.JavaFxBridge;
import com.mk.multiscalemodeling.project1.model.Cell;
import com.mk.multiscalemodeling.project1.model.CellStatus;
import com.mk.multiscalemodeling.project1.model.Grain;
import com.mk.multiscalemodeling.project1.model.GrainImpl;
import com.mk.multiscalemodeling.project1.model.GrainStatus;
import com.mk.multiscalemodeling.project1.model.Inclusion;
import com.mk.multiscalemodeling.project1.model.InclusionShape.InclusionType;
import com.mk.multiscalemodeling.project1.model.neighbourdhood.Neighbourhood;
import com.mk.multiscalemodeling.project1.simulation.mc.MCRecrystallisationController;
import com.mk.multiscalemodeling.project1.simulation.mc.MonteCarloController;
import com.mk.multiscalemodeling.project1.simulation.mc.RecrystallisedLocationType;
import com.mk.multiscalemodeling.project1.simulation.mc.RecrystallisedNucleatingType;
import com.mk.multiscalemodeling.project1.simulation.mc.energy.EnergyDistributor;
import com.mk.multiscalemodeling.project1.simulation.mc.energy.EnergyType;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
@Component
public class SimulationManager {

    private SimulationStatus simulationStatus;
    private GrainsManager grainsManager;
    private MonteCarloController mcController;
    private MCRecrystallisationController mcRecrystallisationController;
    private EnergyDistributor energyDistributor;
    
    @Getter 
    private int dimX;
    @Getter 
    private int dimY;
    
    //@Getter
    //private boolean fullGrown;
    
    @Getter
    @Setter
    private Cell cells[][];
    
    private List<Cell> listOfNonAbsorbingCells = new ArrayList<>();
    
    public void init(SimulationStatus simulationStatus, int dimX, int dimY) {
        this.simulationStatus = simulationStatus;
        this.dimX = dimX;
        this.dimY = dimY;
        
        //because there are a lot of problems between javaFx and Spring compatibility 
        //TODO: try to resolve problems with compatibility
        grainsManager = JavaFxBridge.applicationContext.getBean(GrainsManager.class);
        grainsManager.init(this);
        
        mcController = new MonteCarloController(this);
        mcRecrystallisationController = new MCRecrystallisationController(this, grainsManager);
        energyDistributor = new EnergyDistributor(this);
        
        if (this.simulationStatus.equals(SimulationStatus.NEW)) {
            initCells();
            log.info("Initialize simulation with params dimX{}, dimY {}", dimX, dimY);
        } else {
            log.info("Initialize loaded simulation with params dimX{}, dimY {}", dimX, dimY);
        }
    }
    
    public void addNucleonsToSimulation(int count) {
        List<Grain> nucleonsToAdd = grainsManager.createNeuclons(count, false);
        
        log.debug("Adding {} neuclons to simulation", count);
        
        Random rand = new Random();
        while (!nucleonsToAdd.isEmpty()) {
            // add 1 to move away from the absorbing edge
            int randomX = 1 + rand.nextInt(dimX);
            int randomY = 1 + rand.nextInt(dimY);
            
            Cell selectedCell = cells[randomX][randomY];
            
            if (selectedCell.getStatus().equals(CellStatus.EMPTY)) {
                selectedCell.setStatus(CellStatus.OCCUPIED);
                selectedCell.setGrain(nucleonsToAdd.remove(0));;
                log.trace("Nucleon added to cell array ({},{})", randomX, randomY);
            }
        }
    }
    
    public void addNucleonToSimulation(Cell cell) {
        if (cell.getStatus().equals(CellStatus.EMPTY)) {
            Grain grain = grainsManager.createNeuclon();
            cell.setStatus(CellStatus.OCCUPIED);
            cell.setGrain(grain);;
            log.trace("Nucleon added to cell array ({},{})", cell.getX(), cell.getY());
        }
    }
    
    public void addGrainsToSimulationMC(int noOfGrains) {
        log.info("Filling the simulation with {} grains", noOfGrains);
        List<Cell> emptyCells = getListOfEmptyCells();
        if (emptyCells.size() < noOfGrains) {
            log.error("Number of empty cells in simulation is smaller than number of grains to add.");
            return;
        }
        
        List<Grain> grains = grainsManager.createNeuclons(noOfGrains, false);
        Random random = new Random();
        
        while (!emptyCells.isEmpty()) {
            for (Grain singleGrain: grains) {
                int sizeOfEmptyCellList = emptyCells.size();
                if (sizeOfEmptyCellList == 0) {
                    break;
                }
                int intedEmptyCell = random.nextInt(sizeOfEmptyCellList);
                Cell cell = emptyCells.remove(intedEmptyCell);
                cell.setGrain(singleGrain);
                cell.setStatus(CellStatus.OCCUPIED);
            }
        }
        
        log.info("Simulation filled");
    }
    
    public void addInclusionsToSimulation(int count, int sizeOfInclusion, InclusionType shape) {
        log.info("Adding {} inclusions to simulation", count);
        
        Random rand = new Random();
        List<Inclusion> inclusions = grainsManager.createInclusion(count);
        
        while (!inclusions.isEmpty()) {
            // add 1 to move away from the absorbing edge
            int randomX = 1 + rand.nextInt(dimX);
            int randomY = 1 + rand.nextInt(dimY);

            Cell selectedCell = cells[randomX][randomY];
            if (selectedCell.getStatus().equals(CellStatus.EMPTY)) {
                shape.genearteInclusion(selectedCell, inclusions.remove(0), sizeOfInclusion);
                log.debug("Adding inclusions to simulation(on empty cell) ({}, {})", randomX, randomY);
            } else if (selectedCell.getStatus().equals(CellStatus.OCCUPIED)) {
                if (checkIfOnGrainsEdge(selectedCell)) {
                    shape.genearteInclusion(selectedCell, inclusions.remove(0), sizeOfInclusion);
                    log.debug("Adding inclusions to simulation (on grains edge) ({}, {})", randomX, randomY);
                }
            }

            log.trace("Inclusion added to cell array ({},{})", randomX, randomY);
        }

    }

    public boolean checkIfOnGrainsEdge(Cell cellToCheck) {
        int x = cellToCheck.getX();
        int y = cellToCheck.getY();
        
        if (cellToCheck.getGrain() == null) {
            return false;
        } 
        
        Grain grain = cellToCheck.getGrain();
        for (int i = (x - 1); i <= (x + 1); i++) {
            for (int j = (y - 1); j <= (y + 1); j++) {
                if ((i == x && y == j) || !isInSimulationRange(i, j) ) {
                    continue;
                }

                Cell cell = cells[i][j];
                if (cell.getStatus().equals(CellStatus.ABSORBING) || cell.getStatus().equals(CellStatus.INCLUSION)) {
                    continue;
                }
                
                if (cell.getStatus().equals(CellStatus.EMPTY) || (!cell.getGrain().equals(grain))) {
                    return true;
                }
            }
        }
        
        return false;
    } 
    
    public boolean simulateGrowth(Neighbourhood neighbourhoodType) {
        boolean hasGrown = false;
        List<Pair<Cell, Grain>> cellsToUpdate = new ArrayList<>();
        log.trace("Perform 1 iteration of simulation");
        for (int i = 1; i < dimX + 1; i ++) {
            for (int j = 1; j < dimY + 1; j++) {
                if (cells[i][j].getStatus().equals(CellStatus.EMPTY)) {
                    Grain matchedGrain = neighbourhoodType.tryToMatchTheGrain(cells[i][j], cells);
                    if (matchedGrain != null) {
                        cellsToUpdate.add(Pair.of(cells[i][j], matchedGrain));
                    }
                }
            }
        }
        
        hasGrown = !cellsToUpdate.isEmpty();
        
        cellsToUpdate.stream().forEach((e) -> {
            updateCells(e.getLeft(), e.getRight());
        });
        log.trace("Cells updated");
        return hasGrown;
    }
    
    public void simulateMC(Double grainBoundaryEnergy) {
        getListOfRandomCells().stream().forEach(cell -> {
            Grain resolvedGrain = mcController.resolveCellMembership(cell, grainBoundaryEnergy);
            if (!cell.getGrain().equals(resolvedGrain)) {
                cell.setGrain(resolvedGrain);
            }
        });
    }
    
    public void addRecrystallisedNucleons(int count, RecrystallisedLocationType locationType) {
        List<Grain> nucleonsToAdd = grainsManager.createNeuclons(count, true);
        
        log.debug("Adding {} recristallised neuclons to simulation", count);
        int iterationLimit = 10000;
        
        Random rand = new Random();
        while (!nucleonsToAdd.isEmpty() && iterationLimit > 0) {
            // add 1 to move away from the absorbing edge
            int randomX = 1 + rand.nextInt(dimX);
            int randomY = 1 + rand.nextInt(dimY);
            
            Cell selectedCell = cells[randomX][randomY];
            
            if (locationType.equals(RecrystallisedLocationType.BORDER) && (!checkIfOnGrainsEdge(selectedCell))) {
                continue;
            }
            
            if (selectedCell.getGrain() != null && selectedCell.getGrain().getStatus().equals(GrainStatus.GRAIN)) {
                selectedCell.setEnergy(0.0);
                selectedCell.setGrain(nucleonsToAdd.remove(0));
                log.trace("Recrystallised nucleon added to cell array ({},{})", randomX, randomY);
            }
            
            iterationLimit--;
        }
    }
    
    public void distributeEnergy(EnergyType energyType, double threshold, double energyInside, double energyGB) {
        energyDistributor.distributeEnergy(listOfNonAbsorbingCells, energyType, threshold, energyInside, energyGB);
    }
    
    public void simulateRecristalization(RecrystallisedLocationType locationType, RecrystallisedNucleatingType nucleatingType, int noOfNucleons, 
            int noOfIterations, double grainBoundaryEnergy) {
        
        mcRecrystallisationController.simulateRecristalization(locationType, nucleatingType, noOfNucleons, noOfIterations, grainBoundaryEnergy);
    }
    
    public void addBorder(Set<GrainImpl> grains, int width) {
        log.info("Adding borders for selected grains");
        grainsManager.addBorder(grains, width);
    }
    
    public void addBorderForAllGrains(int width) {
        log.info("Adding borders for all grains");
        grainsManager.addBorderForAllGrains(width);
    }
    
    public boolean isInSimulationRange(int i, int j) {
        return ((i > 0) && (i < (dimX + 1)) && (j > 0) && (j < (dimY + 1)));
    }
    
    public void clearSimulation() {
        grainsManager.clearAll();
        
        initCells();
    }
    
    public void removeSelectedGrains(Set<GrainImpl> grainsToRemove, boolean removeBorder) {
        grainsManager.removeGrains(grainsToRemove, removeBorder);
    }
    
    public void removeExceptSelectedGrains(Set<GrainImpl> selectedGrains, boolean removeBorder) {
        grainsManager.removeExceptSelected(selectedGrains, removeBorder);
    }
    
    public void mergeSelectedGrains(Set<GrainImpl> selectedGrains) {
        grainsManager.mergeSelectedGrains(selectedGrains);
    }

    public boolean checkIfFullyGrown() {
        for (int i = 1; i < dimX + 1; i ++) {
            for (int j = 1; j < dimY + 1; j++) {
                if (cells[i][j].getStatus().equals(CellStatus.EMPTY)) {
                    log.info("Empty cell on X: {}, Y: {}", i, j);
                    return false;
                }
            }
        }
        
        return true;
    }
    
    public Cell getFromAbove(Cell cell) {
        if (isInSimulationRange(cell.getX(), cell.getY() - 1)) {
            return cells[cell.getX()][cell.getY() - 1];
        }
        return null;
    }
    
    public Cell getFromAboveLeft(Cell cell) {
        if (isInSimulationRange(cell.getX() - 1, cell.getY() - 1)) {
            return cells[cell.getX() - 1][cell.getY() - 1];
        }
        return null;
    }
    
    public Cell getFromLeft(Cell cell) {
        if (isInSimulationRange(cell.getX() - 1, cell.getY())) {
            return cells[cell.getX() - 1][cell.getY()];
        }
        return null;
    }
    
    public Cell getFromBelowLeft(Cell cell) {
        if (isInSimulationRange(cell.getX() - 1, cell.getY() + 1)) {
            return cells[cell.getX() - 1][cell.getY() + 1];
        }
        return null;
    }
    
    public Cell getFromBelow(Cell cell) {
        if (isInSimulationRange(cell.getX(), cell.getY() + 1)) {
            return cells[cell.getX()][cell.getY() + 1];
        }
        return null;
    }
    
    public Cell getFromBelowRight(Cell cell) {
        if (isInSimulationRange(cell.getX() + 1, cell.getY() + 1)) {
            return cells[cell.getX() + 1][cell.getY() + 1];
        }
        return null;
    }
    
    public Cell getFromRight(Cell cell) {
        if (isInSimulationRange(cell.getX() + 1, cell.getY())) {
            return cells[cell.getX() + 1][cell.getY()];
        }
        return null;
    }
    
    public Cell getFromAboveRight(Cell cell) {
        if (isInSimulationRange(cell.getX() + 1, cell.getY() - 1)) {
            return cells[cell.getX() + 1][cell.getY() - 1];
        }
        return null;
    }
    
    public double computeBoundriesOccupation() {
        double simulationArea = dimX * dimY;
        int cellWithBorder = 0;
        for (int i = 0; i < (dimX + 2); i ++) {
            for (int j = 0; j < (dimY + 2); j++) {
                if (cells[i][j].getStatus().equals(CellStatus.BORDER)) {
                    cellWithBorder++;
                }
            }
        }
        
        return  (((double) cellWithBorder) / simulationArea) * 100;
    }
    
    private void updateCells(Cell cellToUpdate, Grain grain) {
        cellToUpdate.setGrain(grain);
        cellToUpdate.setStatus(CellStatus.OCCUPIED);      
    }
    
    private void initCells() {
        cells = new Cell[dimX + 2][dimY + 2];
        for (int i = 0; i < (dimX + 2); i ++) {
            for (int j = 0; j < (dimY + 2); j++) {
                if  (i == 0 || j == 0 || i == (dimX + 1) || j == (dimY + 1)) {
                    cells[i][j] = new Cell(CellStatus.ABSORBING, i, j);
                } else {
                    cells[i][j] = new Cell(CellStatus.EMPTY, i, j);
                    listOfNonAbsorbingCells.add(cells[i][j]);
                }
            }
        }
    }
    
    private List<Cell> getListOfEmptyCells() {
        List<Cell> emptyCells = new ArrayList<>(dimX * dimY); 
        for (int i = 1; i < (dimX + 1); i++) {
            for (int j = 1; j < (dimY + 1); j++) {
                Cell cell = cells[i][j];
                if (cell.getStatus().equals(CellStatus.EMPTY)) {
                    emptyCells.add(cell);
                }
            }
        }
        
        return emptyCells;
    }

    public List<Cell> getListOfRandomCells() {
        List<Cell> orderedCells = new ArrayList<>(listOfNonAbsorbingCells);
        List<Cell> randomCells = new ArrayList<>();
        
        Random rand = new Random();
        while (!orderedCells.isEmpty()) {     
            Cell randomCell = orderedCells.remove(rand.nextInt(orderedCells.size()));
            randomCells.add(randomCell);
        }
        
        return randomCells;
    }
    
    public Pair<Double, Double> getMinMaxEnergy() {
        return energyDistributor.getMinMaxEnergy(listOfNonAbsorbingCells);
    }
}
