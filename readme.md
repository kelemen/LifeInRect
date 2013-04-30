Life in Rect is a simulation of entities living within a rectangular world with a certain set of rules. The simulation uses a kind of evolutionary algorithm.

Entities of the world
---------------------

Entities are defined by an [MLP](http://en.wikipedia.org/wiki/Multilayer_perceptron).

### Properties of the MLP ###

- Single hidden layer with 10 neurons, neurons have bias neurons as well (constant input)
- Hidden layer have a sigmoid activation function: 1 / (1 + e^(-x))
- 9 input neurons: One of them is the "mental state" and others are the relative appearances of its 8 neighbours.
  The relative appearance is calculated simply by subtracting the "appearance" of this entity (from the appearance of its neighbour).
  If a neighbour is empty then its appearance is consireder zero (just like an entity with the same appearance).
- 11 output neurons:
  - Appearance, always calculated with constant 1.0 on all input nodes.
  - The new mental state of this entity after a tick of the world.
  - 9 nodes describing the chosen action: attack neighbour or do nothing. The one with the highest value is chosen.

All the weights in the MLP make up the genes of the entity and this is used by the world to breed new entities when needed.


Rules of the world
------------------

0. Random entities are generated with genes between -1 and 1 (uniformly distributed).
1. Entities choose their action.
2. Resolve the fights caused by the actions of the entities. A fight is always deadly for exactly one participant.
  - Defender has a chance an (attacker count) / (attacker count + 1) to die by default, this is multiplied by the
    so called defender chance multiplier (further reducing its chances of survival because it is a value within [0, 1]).
  - If the defender survives a single attacker dies (chosen uniformly from the attackers).
3. Resolve accidents: For each remaining entities there is a chance to die (specified on the GUI).
4. Breed new entities to empty places: For each empty place in the world choose a neighbour entity randomly
   and then choose a neighbour of that entity randomly. If it cannot be done then this place remains empty
   in this tick. Breeding uses a one-point crossover with a mutation rate (chance of gene mutation) given on the GUI.
5. Repeat from 1.


Results
-------

I was curious what will become of this entities in this harsh world. Displaying the appearances of the entities
it is apparent that entities with similar appearances will stick together. This could be the result of many things,
so I also decided to gather some information of the population.

### Aggressiveness of population ###

By aggressiveness, I mean the chance an entity takes an attack action (instead of doing nothing).
There seems to be two stages of the evolution. In the first stage (very early), entities become relatively agressive
and in the second stage (after 100-200 ticks) the aggressiveness seems to dimish.

### Racism of population ###

By racism, I mean the chance an entity chooses to attack another entity if it is surrounded by entities with the same
appearance as its own except for one.

The entities become clearly racist after a very short period of time. The same applies to racism as to aggressiveness
(increasing racism in the first stage, decreasing in the second). Racism is particulary interesting because appearance
does not give an advantage to the entity. Also, similar appearance doesn't necessarily means that the genes of the
entities are similar (in fact they can as much different as another entity with a different appearance). Yet they
choose to use appearance as a form of communicating their similarity of genes.


Example screen shots
--------------------

The download section contains a zipped version of some screen shots of several stages of the world. The graphs
are set to a constant scale in order for the screen shots to be comparable (this makes it a little hard to see that
aggressiveness against entities with different appearance are clearly higher).
