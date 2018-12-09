package com.pausanchezv.puzzle.model;

/**
 *
 * @author pausanchezv
 */
public final class Distribution {

    // Percentages
    private final int blockPercentage;
    private final int bishopPercentage;
    private final int queenPercentage;
    private final int towerPercentage;

    /**
     * Distribution Constructor
     * @param blockPercentage int
     * @param bishopPercentage int
     * @param queenPercentage int
     */
    public Distribution(int blockPercentage, int bishopPercentage, int queenPercentage) {

        this.blockPercentage = blockPercentage;
        this.bishopPercentage = bishopPercentage;
        this.queenPercentage = queenPercentage;
        this.towerPercentage = addTowerPercentage();

    }

    /**
     * Add tower percentage
     * @return int
     */
    private int addTowerPercentage() {
        return 100 - this.blockPercentage - this.bishopPercentage - this.queenPercentage;
    }

    /**
     * Block percentage
     * @return int
     */
    public int getBlockPercentage() {
        return blockPercentage;
    }

    /**
     * Bishop percentage
     * @return int
     */
    public int getBishopPercentage() {
        return bishopPercentage;
    }

    /**
     * Queen percentage
     * @return int
     */
    public int getQueenPercentage() {
        return queenPercentage;
    }

    /**
     * Tower percentage
     * @return int
     */
    public int getTowerPercentage() {
        return towerPercentage;
    }

}