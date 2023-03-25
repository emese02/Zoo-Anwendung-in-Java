package repository;

/**
 * CRUD - Contains basic operations: add, delete, update, findByID.
 * @param <ID> Identificator
 * @param <E> Object
 */
public interface ICrudRepository<ID, E>{
    /**
     * This method adds an Object.
     * @param e Element that will be added
     */
    void add(E e);
    /**
     * This method deletes an Object.
     * @param id parametrized type - ID of the Object that will be deleted
     */
    void delete(ID id);
    /**
     * This method updates an Object.
     * @param id parametrized type - ID of the Object that will be updated
     * @param e Object that will appear instead of the old Object
     */
    void update(ID id, E e);
    /**
     * This method searches an Object based on the ID and returns it.
     * @param id parametrized type - ID of the Object that is searched
     * @return E The found Object
     */
    E findByID(ID id);
}
