﻿/*
 * ** Network and Service Differentiation Extensions to CloudSim 3.0 **
 *
 * Gokul Poduval & Chen-Khong Tham
 * Computer Communication Networks (CCN) Lab
 * Dept of Electrical & Computer Engineering
 * National University of Singapore
 * August 2004
 *
 * Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 * Copyright (c) 2004, The University of Melbourne, Australia and National
 * University of Singapore
 * InfoPacket.java - Implementation of a Information Packet.
 *
 */

package org.cloudbus.cloudsim;

import java.text.DecimalFormat;
import java.util.Vector;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;

/**
 * InfoPacket class can be used to gather information from the network layer.
 * An InfoPacket traverses the network topology similar to a
 * {@link gridsim.net.NetPacket},
 * but it collects information like bandwidths, and Round Trip Time etc. It is
 * the equivalent of ICMP in physical networks.
 * <p>
 * You can set all the parameters to an InfoPacket that can be applied to a
 * NetPacket. So if you want to find out the kind of information that a
 * particular type of NetPacket is experiencing, set the size and network
 * class of an InfoPacket to the same as the NetPacket, and send it to the same
 * destination from the same source.
 *
 * @author		Gokul Poduval
 * @author		Chen-Khong Tham, National University of Singapore
 * @since		CloudSim Toolkit 1.0
 */
public class InfoPacket implements Packet {

    /** The name. */
    private final String name;   // packet name

    /** The size. */
    private long size;     // size of this packet

    /** The packet id. */
    private final int packetId;  // id of this packet

    /** The src id. */
    private final int srcId;     // original sender id

    /** The dest id. */
    private int destId;    // destination id

    /** The last. */
    private int last;      // last hop

    /** The tag. */
    private int tag;       // whether going or returning

    /** The hops number. */
    private int hopsNumber;    // number of hops

    /** The ping size. */
    private long pingSize; // original ping size

    /** The net service type. */
    private int netServiceType;    // level of service type

    /** The bandwidth. */
    private double bandwidth;      // bottleneck

    /** The entities. */
    private Vector<Integer> entities;       // list of entity IDs

    /** The entry times. */
    private Vector<Double> entryTimes;     // list of entry times

    /** The exit times. */
    private Vector<Double> exitTimes;      // list of exit times

    /** The baud rates. */
    private Vector<Double> baudRates;      // list of entity's baud rate

    /** The num. */
    private DecimalFormat num;     // formatting the decimal points

    /**
     * Constructs a new Information packet.
     *
     * @param name   Name of this packet
     * @param packetID  The ID of this packet
     * @param size   size of the packet
     * @param srcID  The ID of the entity that sends out this packet
     * @param destID The ID of the entity to which this packet is destined
     * @param netServiceType the class of traffic this packet belongs to
     *
     * @pre name != null
     * @post $none
     */
	public InfoPacket(String name, int packetID, long size, int srcID,
			int destID, int netServiceType) {
		this.name = name;
		this.packetId = packetID;
		this.srcId = srcID;
		this.destId = destID;
		this.size = size;
		this.netServiceType = netServiceType;

		init();
	}

    /**
     * Initialises common attributes.
     *
     * @pre $none
     * @post $none
     */
	private void init() {
		this.last = srcId;
		this.tag = CloudSimTags.INFOPKT_SUBMIT;
		this.bandwidth = -1;
		this.hopsNumber = 0;
		this.pingSize = this.size;

		if (name != null) {
			this.entities = new Vector<Integer>();
			this.entryTimes = new Vector<Double>();
			this.exitTimes = new Vector<Double>();
			this.baudRates = new Vector<Double>();
			num = new DecimalFormat("#0.000#"); // 4 decimal spaces
		}
	}

    /**
     * Returns the ID of this packet.
     *
     * @return packet ID
     *
     * @pre $none
     * @post $none
     */
    @Override
	public int getId() {
        return packetId;
    }

    /**
     * Sets original size of ping request.
     *
     * @param size  ping data size (in bytes)
     *
     * @pre size >= 0
     * @post $none
     */
    public void setOriginalPingSize(long size) {
        this.pingSize = size;
    }

    /**
     * Gets original size of ping request.
     *
     * @return original size
     *
     * @pre $none
     * @post $none
     */
    public long getOriginalPingSize() {
        return pingSize;
    }

    /**
     * Returns a human-readable information of this packet.
     *
     * @return description of this packet
     *
     * @pre $none
     * @post $none
     */
	@Override
	public String toString() {
        if (name == null) {
            return "Empty InfoPacket that contains no ping information.";
        }

        int SIZE = 1000;   // number of chars
        StringBuffer sb = new StringBuffer(SIZE);
        sb.append("Ping information for " + name + "\n");
        sb.append("Entity Name\tEntry Time\tExit Time\t Bandwidth\n");
        sb.append("----------------------------------------------------------\n");

        String tab = "    ";  // 4 spaces
        for (int i = 0 ; i < entities.size(); i++)
        {
            int resID = entities.get(i).intValue();
            sb.append(CloudSim.getEntityName(resID) + "\t\t");

            String entry = getData(entryTimes, i);
            String exit = getData(exitTimes, i);
            String bw = getData(baudRates, i);

            sb.append(entry + tab + tab + exit + tab + tab + bw + "\n");
        }

        sb.append("\nRound Trip Time : " +
                  num.format(this.getTotalResponseTime()) );
        sb.append(" seconds");
        sb.append("\nNumber of Hops  : " + this.getNumHop() );
        sb.append("\nBottleneck Bandwidth : " + bandwidth + " bits/s");
        return sb.toString();
    }

    /**
     * Gets relevant data from a list.
     *
     * @param v  a list
     * @param index   the location in a list
     *
     * @return the data
     *
     * @pre v != null
     * @post index > 0
     */
	private String getData(Vector<Double> v, int index) {
		String result;
		try {
			Double obj = v.get(index);
			double id = obj.doubleValue();
			result = num.format(id);
		} catch (Exception e) {
			result = "    N/A";
		}

		return result;
	}

    /**
     * Gets the size of this packet.
     *
     * @return size of the packet.
     *
     * @pre $none
     * @post $none
     */
    @Override
	public long getSize() {
        return size;
    }

    /**
     * Sets the size of this packet.
     *
     * @param size  size of the packet
     *
     * @return <tt>true</tt> if it is successful, <tt>false</tt> otherwise
     *
     * @pre size >= 0
     * @post $none
     */
	@Override
	public boolean setSize(long size) {
		if (size < 0) {
			return false;
		}

		this.size = size;
		return true;
	}

    /**
     * Gets the id of the entity to which the packet is destined.
     *
     * @return the desination ID
     *
     * @pre $none
     * @post $none
     */
    @Override
	public int getDestId() {
        return destId;
    }

    /**
     * Gets the id of the entity that sent out this packet.
     *
     * @return the source ID
     *
     * @pre $none
     * @post $none
     */
    @Override
	public int getSrcId() {
        return srcId;
    }

    /**
     * Returns the number of hops that this packet has traversed. Since the
     * packet takes a round trip, the same router may have been traversed
     * twice.
     *
     * @return the number of hops this packet has traversed
     *
     * @pre $none
     * @post $none
     */
	public int getNumHop() {
		int PAIR = 2;
		return ((hopsNumber - PAIR) + 1) / PAIR;
	}

    /**
     * Gets the total time that this packet has spent in the network. This is
     * basically the RTT. Dividing this by half should be the approximate
     * latency.
     * <p>
     * RTT is taken as the final entry time - first exit time.
     *
     * @return total round time
     *
     * @pre $none
     * @post $none
     */
	public double getTotalResponseTime() {
		if (exitTimes == null || entryTimes == null) {
			return 0;
		}

		double time = 0;
		try {
			double startTime = exitTimes.firstElement().doubleValue();
			double receiveTime = entryTimes.lastElement().doubleValue();
			time = receiveTime - startTime;
		} catch (Exception e) {
			time = 0;
		}

		return time;
	}

    /**
     * Returns the bottleneck bandwidth between the source and the destination.
     *
     * @return the bottleneck bandwidth
     *
     * @pre $none
     * @post $none
     */
    public double getBaudRate() {
        return bandwidth;
    }

    /**
     * This method should be called by network entities that count as hops, for
     * e.g. Routers or CloudResources. It should not be called by links etc.
     *
     * @param id    the id of the hop that this InfoPacket is traversing
     *
     * @pre id > 0
     * @post $none
     */
	public void addHop(int id) {
		if (entities == null) {
			return;
		}

		hopsNumber++;
		entities.add(Integer.valueOf(id));
	}

    /**
     * This method should be called by routers and other entities
     * when this InfoPacket reaches them along with the current simulation time.
     *
     * @param time  current simulation time, use
     * {@link gridsim.CloudSim#clock()} to obtain this
     *
     * @pre time >= 0
     * @post $none
     */
	public void addEntryTime(double time) {
		if (entryTimes == null) {
			return;
		}

		if (time < 0) {
			time = 0.0;
		}

		entryTimes.add(Double.valueOf(time));
	}

    /**
     * This method should be called by routers and other entities
     * when this InfoPacket is leaving them. It should also supply the current
     * simulation time.
     *
     * @param time  current simulation time, use
     * {@link gridsim.CloudSim#clock()} to obtain this
     *
     * @pre time >= 0
     * @post $none
     */
	public void addExitTime(double time) {
		if (exitTimes == null) {
			return;
		}

		if (time < 0) {
			time = 0.0;
		}

		exitTimes.add(Double.valueOf(time));
	}

    /**
     * Every entity that the InfoPacket traverses should add the baud rate of
     * the link on which this packet will be sent out next.
     *
     * @param baudRate  the entity's baud rate in bits/s
     *
     * @pre baudRate > 0
     * @post $none
     */
	public void addBaudRate(double baudRate) {
		if (baudRates == null) {
			return;
		}

		baudRates.add(new Double(baudRate));
		if (bandwidth < 0 || baudRate < this.bandwidth) {
			this.bandwidth = baudRate;
		}
	}

    /**
     * Returns the list of all the bandwidths that this packet has traversed.
     *
     * @return a Double Array of links bandwidths
     *
     * @pre $none
     * @post $none
     */
	public Object[] getDetailBaudRate() {
		if (baudRates == null) {
			return null;
		}

		return baudRates.toArray();
	}

    /**
     * Returns the list of all the hops that this packet has traversed.
     *
     * @return an Integer Array of hop ids
     *
     * @pre $none
     * @post $none
     */
	public Object[] getDetailHops() {
		if (entities == null) {
			return null;
		}

		return entities.toArray();
	}

    /**
     * Returns the list of all entry time that this packet has traversed.
     *
     * @return an Integer Array of entry time
     *
     * @pre $none
     * @post $none
     */
	public Object[] getDetailEntryTimes() {
		if (entryTimes == null) {
			return null;
		}

		return entryTimes.toArray();
	}

    /**
     * Returns the list of all exit time that this packet has traversed.
     *
     * @return an Integer Array of exit time
     *
     * @pre $none
     * @post $none
     */
	public Object[] getDetailExitTimes() {
		if (exitTimes == null) {
			return null;
		}

		return exitTimes.toArray();
	}

    /**
     * Gets an entity ID from the last hop that this packet has traversed.
     *
     * @return an entity ID
     *
     * @pre $none
     * @post $none
     */
    @Override
	public int getLast() {
        return last;
    }

    /**
     * Sets an entity ID from the last hop that this packet has traversed.
     *
     * @param last  an entity ID from the last hop
     *
     * @pre last > 0
     * @post $none
     */
    @Override
	public void setLast(int last) {
        this.last = last;
    }

    /**
     * Gets the network service type of this packet.
     *
     * @return the network service type
     *
     * @pre $none
     * @post $none
     */
    @Override
	public int getNetServiceType() {
        return netServiceType ;
    }

    /**
     * Sets the network service type of this packet.
     *
     * @param netServiceType    the packet's network service type
     *
     * @pre netServiceType >= 0
     * @post $none
     */
    @Override
	public void setNetServiceType(int netServiceType) {
        this.netServiceType = netServiceType ;
    }

    /**
     * Gets this packet tag.
     *
     * @return this packet tag
     *
     * @pre $none
     * @post $none
     */
    @Override
	public int getTag() {
        return tag ;
    }

    /**
     * Sets the tag of this packet.
     *
     * @param tag   the packet's tag
     *
     * @return <tt>true</tt> if successful, <tt>false</tt> otherwise
     *
     * @pre tag > 0
     * @post $none
     */
	public boolean setTag(int tag) {
		boolean flag = false;
		switch (tag) {
		case CloudSimTags.INFOPKT_SUBMIT:
		case CloudSimTags.INFOPKT_RETURN:
			this.tag = tag;
			flag = true;
			break;

		default:
			flag = false;
			break;
		}

		return flag;
	}

    /**
     * Sets the destination ID for this packet.
     *
     * @param id    this packet's destination ID
     *
     * @pre id > 0
     * @post $none
     */
    public void setDestId(int id) {
        this.destId = id;
    }

}
