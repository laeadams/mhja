package dd.mhja.dao;

import java.util.Optional;
import javax.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegionDaoSimple {
	static protected final Logger LOG = LoggerFactory.getLogger(RegionDaoSimple.class);

	public Optional<Region> read(Integer id) {
		EntityManager em = null;

		try {
			em = JpaUtil.getEntityManager();
			Region region = em.find(Region.class, id);
			return Optional.ofNullable(region);
		} catch (Exception ex) {
			LOG.error("Can't create query: " + ex.getMessage());
			throw ex;
		} finally {
			if (em != null) {
				em.close();
			}
		}
	}

}
